package com.revolut.account.repository

import com.revolut.account.domain.Book
import com.revolut.account.domain.Currency

import java.time.Instant
import java.time.OffsetDateTime

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS
import static com.revolut.account.jooq.tables.Books.BOOKS
import static java.time.ZoneOffset.UTC

class JooqBookRepositoryTest extends JooqRepositoryTestAbstract {
    def repository = new JooqBookRepository(dslContext)

    def 'on save book, should store details in books table'() {
        given:
        def book = Book.builder()
                .id(UUID.randomUUID())
                .credit(10_000.50)
                .debit(null)
                .sourceAccount(UUID.randomUUID())
                .targetAccount(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(book.sourceAccount)
        insertAccount(book.targetAccount)

        when:
        def savedBook = repository.save(book)

        then:
        savedBook == book
        assertBookIsInBooksTable(book)
    }

    void assertBookIsInBooksTable(Book book) {
        def savedBook = selectBook(book.id)

        assert savedBook
        assert savedBook.id == book.id
        assert savedBook.credit == book.credit
        assert savedBook.debit == book.debit
        assert savedBook.sourceAccount == book.sourceAccount
        assert savedBook.targetAccount == book.targetAccount
        assert savedBook.creationDate.toInstant() == book.creationDate
    }

    def insertAccount(UUID id) {
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, id)
                .set(ACCOUNTS.CUSTOMER_ID, UUID.randomUUID())
                .set(ACCOUNTS.CURRENCY, Currency.PHP.toString())
                .set(ACCOUNTS.CREATION_DATE, OffsetDateTime.now(UTC))
                .execute()
    }

    def selectBook(UUID book) {
        dslContext.selectFrom(BOOKS)
                .where(BOOKS.ID.eq(book))
                .fetchAny()
    }

}
