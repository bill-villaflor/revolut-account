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
                .amount(10_000.50)
                .source(UUID.randomUUID())
                .destination(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(bookEntry.source)
        insertAccount(bookEntry.destination)

        when:
        def savedBookEntry = repository.save(bookEntry)

        then:
        savedBookEntry == bookEntry
        assertBookEntryIsInBooksTable(bookEntry)
    }

    def 'on save book entries, should store details in books table'() {
        given:
        def firstBookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .amount(10_000.50)
                .source(UUID.randomUUID())
                .destination(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        def secondBookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .amount(500.50)
                .source(UUID.randomUUID())
                .destination(UUID.randomUUID())
                .creationDate(Instant.now())
                .build()

        insertAccount(firstBookEntry.source)
        insertAccount(firstBookEntry.destination)
        insertAccount(secondBookEntry.source)
        insertAccount(secondBookEntry.destination)

        when:
        def savedBookEntries = repository.saveAll([firstBookEntry, secondBookEntry])

        then:
        savedBookEntries == [firstBookEntry, secondBookEntry]
        assertBookEntryIsInBooksTable(firstBookEntry)
        assertBookEntryIsInBooksTable(secondBookEntry)
    }

    def 'on find book of an account, should return book with credits and debits'() {
        given:
        def firstAccount = UUID.randomUUID()
        def secondAccount = UUID.randomUUID()

        def bookEntries = [
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .amount(1_000.0000)
                        .source(secondAccount)
                        .destination(firstAccount)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .amount(1_000.0000)
                        .source(secondAccount)
                        .destination(firstAccount)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .amount(10_000.0000)
                        .source(firstAccount)
                        .destination(secondAccount)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .amount(15_000.5000)
                        .source(firstAccount)
                        .destination(secondAccount)
                        .creationDate(Instant.now())
                        .build(),
                BookEntry.builder()
                        .id(UUID.randomUUID())
                        .amount(200.2500)
                        .source(firstAccount)
                        .destination(secondAccount)
                        .creationDate(Instant.now())
                        .build()
        ]

        def credits = bookEntries.findAll { entry -> entry.destination == firstAccount }
                .collect { entry -> new Credit(entry.amount, entry.creationDate) }
        def debits = bookEntries.findAll { entry -> entry.source == firstAccount }
                .collect { entry -> new Debit(entry.amount, entry.creationDate) }

        insertAccount(firstAccount)
        insertAccount(secondAccount)

        bookEntries.each { bookEntry -> insertBookEntry(bookEntry) }

        expect:
        repository.find(firstAccount) == new Book(credits, debits)
    }

    void assertBookEntryIsInBooksTable(BookEntry bookEntry) {
        def savedBook = selectBookEntry(bookEntry.id)

        assert savedBook
        assert savedBook.id == bookEntry.id
        assert savedBook.amount == bookEntry.amount
        assert savedBook.source == bookEntry.source
        assert savedBook.destination == bookEntry.destination
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
        record.setAmount(bookEntry.amount)
        record.setSource(bookEntry.source)
        record.setDestination(bookEntry.destination)
        record.setCreationDate(bookEntry.creationDate.atOffset(UTC))
        record.store()
    }
}
