package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.revolut.account.jooq.tables.Books.BOOKS;
import static java.time.ZoneOffset.UTC;

public class JooqBookRepository implements BookRepository {
    private final DSLContext dslContext;

    public JooqBookRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Book save(Book book) {
        dslContext.insertInto(BOOKS)
                .set(BOOKS.ID, book.getId())
                .set(BOOKS.CREDIT, book.getCredit())
                .set(BOOKS.DEBIT, book.getDebit())
                .set(BOOKS.ACCOUNT_ID, book.getAccount())
                .set(BOOKS.CREATION_DATE, book.getCreationDate().atOffset(UTC))
                .execute();

        return book;
    }

    @Override
    public List<BigDecimal> findCredits(UUID account) {
        return dslContext.fetch(BOOKS, BOOKS.ACCOUNT_ID.eq(account).and(BOOKS.CREDIT.isNotNull()))
                .getValues(BOOKS.CREDIT);
    }

    @Override
    public List<BigDecimal> findDebits(UUID account) {
        return dslContext.fetch(BOOKS, BOOKS.ACCOUNT_ID.eq(account).and(BOOKS.DEBIT.isNotNull()))
                .getValues(BOOKS.DEBIT);
    }
}