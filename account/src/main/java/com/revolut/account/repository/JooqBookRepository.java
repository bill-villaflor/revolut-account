package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import com.revolut.account.domain.BookEntry;
import com.revolut.account.domain.Credit;
import com.revolut.account.domain.Debit;
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

public class JooqBookRepository implements BookRepository {
    private final DSLContext create;

    public JooqBookRepository(DSLContext create) {
        this.create = create;
    }

    @Override
    public BookEntry save(BookEntry bookEntry) {
        create.insertInto(BOOKS)
                .set(BOOKS.ID, bookEntry.getId())
                .set(BOOKS.CREDIT, bookEntry.getCredit())
                .set(BOOKS.DEBIT, bookEntry.getDebit())
                .set(BOOKS.ACCOUNT_ID, bookEntry.getAccount())
                .set(BOOKS.CREATION_DATE, bookEntry.getCreationDate().atOffset(UTC))
                .execute();

        return bookEntry;
    }

    @Override
    public Book find(UUID account) {
        try (Cursor cursor = create.fetchLazy(BOOKS, BOOKS.ACCOUNT_ID.eq(account))) {
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
