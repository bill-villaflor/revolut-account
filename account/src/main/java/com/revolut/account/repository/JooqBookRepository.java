package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import com.revolut.account.domain.BookEntry;
import com.revolut.account.domain.Credit;
import com.revolut.account.domain.Debit;
import com.revolut.account.jooq.tables.records.BooksRecord;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.revolut.account.jooq.tables.Books.BOOKS;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class JooqBookRepository implements BookRepository {
    private final DSLContext create;

    public JooqBookRepository(DSLContext create) {
        this.create = create;
    }

    @Override
    public BookEntry save(BookEntry bookEntry) {
        toBooksRecord(bookEntry).store();

        return bookEntry;
    }

    @Override
    public List<BookEntry> saveAll(List<BookEntry> bookEntries) {
        List<BooksRecord> records = bookEntries.stream()
                .map(this::toBooksRecord)
                .collect(toList());

        create.batchInsert(records).execute();

        return bookEntries;
    }

    @Override
    public Book find(UUID account) {
        try (Cursor cursor = findBookEntriesByAccount(account)) {
            List<Credit> credits = new ArrayList<>();
            List<Debit> debits = new ArrayList<>();

            while (cursor.hasNext()) {
                Record record = cursor.fetchNext();

                if (nonNull(record.get(BOOKS.DEBIT))) {
                    debits.add(toDebit(record));
                } else if (nonNull(record.get(BOOKS.CREDIT))) {
                    credits.add(toCredit(record));
                }
            }

            return new Book(credits, debits);
        }
    }

    private BooksRecord toBooksRecord(BookEntry bookEntry) {
        BooksRecord record = create.newRecord(BOOKS);
        record.setId(bookEntry.getId());
        record.setCredit(bookEntry.getCredit());
        record.setDebit(bookEntry.getDebit());
        record.setAccountId(bookEntry.getAccount());
        record.setCreationDate(bookEntry.getCreationDate().atOffset(UTC));

        return record;
    }

    private Cursor findBookEntriesByAccount(UUID account) {
        return create.select(BOOKS.DEBIT, BOOKS.CREDIT, BOOKS.CREATION_DATE)
                .from(BOOKS)
                .where(BOOKS.ACCOUNT_ID.eq(account))
                .fetchLazy();
    }

    private Debit toDebit(Record record) {
        BigDecimal amount = record.get(BOOKS.DEBIT);
        Instant date = record.get(BOOKS.CREATION_DATE).toInstant();

        return new Debit(amount, date);
    }

    private Credit toCredit(Record record) {
        BigDecimal amount = record.get(BOOKS.CREDIT);
        Instant date = record.get(BOOKS.CREATION_DATE).toInstant();

        return new Credit(amount, date);
    }
}
