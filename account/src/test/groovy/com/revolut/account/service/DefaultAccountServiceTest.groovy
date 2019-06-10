package com.revolut.account.service

import com.revolut.account.domain.Account
import com.revolut.account.domain.Book
import com.revolut.account.domain.BookEntry
import com.revolut.account.domain.Credit
import com.revolut.account.domain.Currency
import com.revolut.account.exception.AccountNotFoundException
import com.revolut.account.exception.InsufficientBalanceException
import com.revolut.account.exception.SourceAccountNotFoundException
import com.revolut.account.repository.AccountRepository
import com.revolut.account.repository.BookRepository
import spock.lang.Specification

import java.time.Instant

class DefaultAccountServiceTest extends Specification {
    def accountRepository = Stub(AccountRepository)
    def bookRepository = Stub(BookRepository)
    def service = new DefaultAccountService(accountRepository, bookRepository)

    def 'on create account with initial balance, should save account and initial book entry'() {
        given:
        def account = Account.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(10_500.50)
                .build()

        def savedAccount = Account.builder().build()
        def savedBookEntry = BookEntry.builder().build()

        accountRepository.save(_ as Account) >> { Account a ->
            savedAccount = a
            savedAccount
        }

        bookRepository.save(_ as BookEntry) >> { BookEntry b ->
            savedBookEntry = b
            savedBookEntry
        }

        when:
        def returnedAccount = service.create(account)

        then:
        returnedAccount == savedAccount
        savedBookEntry.id
        savedBookEntry.credit == savedAccount.balance
        !savedBookEntry.debit
        savedBookEntry.account == savedAccount.id
        savedBookEntry.creationDate == savedAccount.creationDate
    }

    def 'on create account without balance, should only save account'() {
        given:
        def account = Account.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(balance)
                .build()

        def savedAccount = Account.builder().build()

        accountRepository.save(_ as Account) >> { Account a ->
            savedAccount = a
            savedAccount
        }

        when:
        def returnedAccount = service.create(account)

        then:
        returnedAccount == savedAccount

        where:
        balance | _
        null    | _
        0.00    | _
    }

    def 'on get account by id, should return saved account with computed balance from repository'() {
        given:
        def account = Account.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .build()

        def book = new Book([new Credit(10_000.00, Instant.now())], [])

        accountRepository.find(account.id) >> account
        bookRepository.find(account.id) >> book

        when:
        def returnedAccount = service.get(account.id)

        then:
        returnedAccount == account.withBalance(book.balance)
    }

    def 'on get a non-existent account, should throw exception'() {
        given:
        def account = UUID.randomUUID()
        accountRepository.find(account) >> null

        when:
        service.get(account)

        then:
        thrown(AccountNotFoundException)
    }

    def 'on credit account, should return created book entry'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def book = new Book([new Credit(10_000.50, Instant.now())], [])
        def sourceAccount = UUID.randomUUID()
        def savedBooksEntries = new ArrayList<BookEntry>()

        accountRepository.filterExisting(_ as List) >> [bookEntry.account, sourceAccount]
        bookRepository.find(sourceAccount) >> book
        bookRepository.saveAll(_ as List) >> { List args ->
            savedBooksEntries = args.first()
            savedBooksEntries
        }

        when:
        def returnedBookEntry = service.credit(bookEntry, sourceAccount)

        then:
        returnedBookEntry == savedBooksEntries.find { b -> b.credit }
        savedBooksEntries.size() == 2
        savedBooksEntries.find { b -> b.debit }
    }

    def 'on credit to non-existent account, should throw exception'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepository.find(bookEntry.account) >> null

        when:
        service.credit(bookEntry, sourceAccount)

        then:
        thrown(AccountNotFoundException)
    }

    def 'on credit from non-existent account, should throw exception'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepository.filterExisting(_ as List) >> [bookEntry.account]

        when:
        service.credit(bookEntry, sourceAccount)

        then:
        thrown(SourceAccountNotFoundException)
    }

    def 'on credit from account with insufficient balance, should throw exception'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(500.00)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def book = new Book([new Credit(400.00, Instant.now())], [])

        def sourceAccount = UUID.randomUUID()

        accountRepository.filterExisting(_ as List) >> [bookEntry.account, sourceAccount]
        bookRepository.find(sourceAccount) >> book

        when:
        service.credit(bookEntry, sourceAccount)

        then:
        thrown(InsufficientBalanceException)
    }
}