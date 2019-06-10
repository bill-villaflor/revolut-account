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
    public Book find(UUID account) {
        try (Cursor cursor = findBookEntriesByAccount(account)) {
            List<Credit> credits = new ArrayList<>();
            List<Debit> debits = new ArrayList<>();

            while (cursor.hasNext()) {
                Record record = cursor.fetchNext();

                if (account.equals(record.get(BOOKS.SOURCE))) {
                    debits.add(toDebit(record));
                }

                if (account.equals(record.get(BOOKS.DESTINATION))) {
                    credits.add(toCredit(record));
                }
            }

            return new Book(credits, debits);
        }
    }

    private BooksRecord toBooksRecord(BookEntry bookEntry) {
        BooksRecord record = create.newRecord(BOOKS);
        record.setId(bookEntry.getId());
        record.setAmount(bookEntry.getAmount());
        record.setSource(bookEntry.getSource());
        record.setDestination(bookEntry.getDestination());
        record.setCreationDate(bookEntry.getCreationDate().atOffset(UTC));

        return record;
    }

    private Cursor findBookEntriesByAccount(UUID account) {
        return create.select(BOOKS.AMOUNT, BOOKS.SOURCE, BOOKS.DESTINATION, BOOKS.CREATION_DATE)
                .from(BOOKS)
                .where(BOOKS.SOURCE.eq(account)
                        .or(BOOKS.DESTINATION.eq(account)))
                .fetchLazy();
    }

    private Debit toDebit(Record record) {
        BigDecimal amount = record.get(BOOKS.AMOUNT);
        Instant date = record.get(BOOKS.CREATION_DATE).toInstant();

        return new Debit(amount, date);
    }

    private Credit toCredit(Record record) {
        BigDecimal amount = record.get(BOOKS.AMOUNT);
        Instant date = record.get(BOOKS.CREATION_DATE).toInstant();

        return new Credit(amount, date);
    }
}
