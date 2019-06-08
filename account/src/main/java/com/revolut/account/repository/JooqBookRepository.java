package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import org.jooq.DSLContext;

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
                .set(BOOKS.SOURCE_ACCOUNT, book.getSourceAccount())
                .set(BOOKS.TARGET_ACCOUNT, book.getTargetAccount())
                .set(BOOKS.CREATION_DATE, book.getCreationDate().atOffset(UTC))
                .execute();

        return book;
    }
}
