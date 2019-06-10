package com.revolut.account.repository

import com.revolut.account.domain.Book
import com.revolut.account.domain.BookEntry
import com.revolut.account.domain.Credit
import com.revolut.account.domain.Currency
import com.revolut.account.domain.Debit

import java.time.Instant
import java.time.OffsetDateTime

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS
import static com.revolut.account.jooq.tables.Books.BOOKS
import static java.time.ZoneOffset.UTC

class JooqBookRepositoryTest extends JooqRepositoryTestAbstract {
    def repository = new JooqBookRepository(create)

    def 'on save book, should store details in books table'() {
        given:
        def bookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .credit(10_000.50)
                .debit(null)
                .account(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(bookEntry.account)

        when:
        def savedBookEntry = repository.save(bookEntry)

        then:
        savedBookEntry == bookEntry
        assertBookEntryIsInBooksTable(bookEntry)
    }

    def 'on save book entries, should store details in books table'() {
        given:
        def creditBookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .credit(10_000.50)
                .account(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        def debitBookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .debit(500.50)
                .account(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(creditBookEntry.account)
        insertAccount(debitBookEntry.account)

        when:
        def savedBookEntries = repository.saveAll([creditBookEntry, debitBookEntry])

        then:
        savedBookEntries == [creditBookEntry, debitBookEntry]
        assertBookEntryIsInBooksTable(creditBookEntry)
        assertBookEntryIsInBooksTable(debitBookEntry)
    }

    def 'on find book of an account, should return book with credits and debits'() {
        given:
        def account = UUID.randomUUID()

        def bookEntries = [
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .credit(1_000.0000)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .credit(1_000.0000)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .debit(10_000.0000)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .debit(15_000.5000)
                        .account(account)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .debit(200.2500)
                        .account(account)
                        .creationDate(Instant.now())
                        .build()
        ]

        def credits = bookEntries.findAll { entry -> entry.credit }
                .collect { entry -> new Credit(entry.credit, entry.creationDate) }
        def debits = bookEntries.findAll { entry -> entry.debit }
                .collect { entry -> new Debit(entry.debit, entry.creationDate) }

        insertAccount(account)
        bookEntries.each { bookEntry -> insertBookEntry(bookEntry) }

        expect:
        repository.find(account) == new Book(credits, debits)
    }

    void assertBookEntryIsInBooksTable(BookEntry bookEntry) {
        def savedBook = selectBookEntry(bookEntry.id)

        assert savedBook
        assert savedBook.id == bookEntry.id
        assert savedBook.credit == bookEntry.credit
        assert savedBook.debit == bookEntry.debit
        assert savedBook.accountId == bookEntry.account
        assert savedBook.creationDate.toInstant() == bookEntry.creationDate
    }

    def insertAccount(UUID id) {
        create.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, id)
                .set(ACCOUNTS.CUSTOMER_ID, UUID.randomUUID())
                .set(ACCOUNTS.CURRENCY, Currency.PHP.toString())
                .set(ACCOUNTS.CREATION_DATE, OffsetDateTime.now(UTC))
                .execute()
    }

    def selectBookEntry(UUID bookEntry) {
        create.selectFrom(BOOKS)
                .where(BOOKS.ID.eq(bookEntry))
                .fetchAny()
    }

    def insertBookEntry(BookEntry bookEntry) {
        def record = create.newRecord(BOOKS)
        record.setId(bookEntry.id)
        record.setCredit(bookEntry.credit)
        record.setDebit(bookEntry.debit)
        record.setAccountId(bookEntry.account)
        record.setCreationDate(bookEntry.creationDate.atOffset(UTC))
        record.store()
    }
}
