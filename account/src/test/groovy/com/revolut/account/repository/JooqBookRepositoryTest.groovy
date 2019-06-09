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
                .account(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(book.account)

        when:
        def savedBook = repository.save(book)

        then:
        savedBook == book
        assertBookIsInBooksTable(book)
    }

    def 'on find credits of an account, should return list of credited amount'() {
        given:
        def account = UUID.randomUUID()

        def books = [
                Book.builder()
                        .id(UUID.randomUUID())
                        .credit(20_000.00)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .debit(10_000.25)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .credit(30_000.50)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .credit(500.25)
                        .account(account)
                        .creationDate(Instant.now())
                        .build()
        ]

        insertAccount(account)
        books.each { book -> insertBook(book) }

        when:
        def credits = repository.findCredits(account)

        then:
        credits == (books.findAll { book -> book.credit })*.credit
    }

    def 'on find credits of an account without credits, should return empty list'() {
        given:
        def account = UUID.randomUUID()
        insertAccount(account)

        expect:
        repository.findCredits(account) == []
    }

    def 'on find debits of an account, should return list of debited amount'() {
        given:
        def account = UUID.randomUUID()

        def books = [
                Book.builder()
                        .id(UUID.randomUUID())
                        .debit(10_000.00)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .credit(1_000.00)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .debit(15_000.50)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                Book.builder()
                        .id(UUID.randomUUID())
                        .debit(200.25)
                        .account(account)
                        .creationDate(Instant.now())
                        .build()
        ]

        insertAccount(account)
        books.each { book -> insertBook(book) }

        when:
        def debits = repository.findDebits(account)

        then:
        debits == (books.findAll { book -> book.debit })*.debit
    }

    def 'on find debits of an account without debits, should return empty list'() {
        given:
        def account = UUID.randomUUID()
        insertAccount(account)

        expect:
        repository.findDebits(account) == []
    }

    void assertBookIsInBooksTable(Book book) {
        def savedBook = selectBook(book.id)

        assert savedBook
        assert savedBook.id == book.id
        assert savedBook.credit == book.credit
        assert savedBook.debit == book.debit
        assert savedBook.accountId == book.account
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

    def insertBook(Book book) {
        def record = dslContext.newRecord(BOOKS)
        record.setId(book.id)
        record.setCredit(book.credit)
        record.setDebit(book.debit)
        record.setAccountId(book.account)
        record.setCreationDate(book.creationDate.atOffset(UTC))
        record.store()
    }
}
