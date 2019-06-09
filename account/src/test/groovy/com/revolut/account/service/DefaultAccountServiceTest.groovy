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
    def accountRepo = Stub(AccountRepository)
    def bookRepo = Stub(BookRepository)
    def service = new DefaultAccountService(accountRepo, bookRepo)

    def 'on create account with initial balance, should save account and initial book entry'() {
        given:
        def account = Account.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(10_500.50)
                .build()

        def savedAccount = Account.builder().build()
        def savedBookEntry = BookEntry.builder().build()

        accountRepo.save(_ as Account) >> { Account a ->
            savedAccount = a
            savedAccount
        }

        bookRepo.save(_ as BookEntry) >> { BookEntry b ->
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

        accountRepo.save(_ as Account) >> { Account a ->
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

        accountRepo.find(account.id) >> account
        bookRepo.find(account.id) >> book

        when:
        def returnedAccount = service.get(account.id)

        then:
        returnedAccount == account.withBalance(book.balance)
    }

    def 'on get a non-existent account, should throw exception'() {
        given:
        def account = UUID.randomUUID()
        accountRepo.find(account) >> null

        when:
        service.get(account)

        then:
        thrown(AccountNotFoundException)
    }

    def 'on credit account, should return created book'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def book = new Book([new Credit(10_000.50, Instant.now())], [])
        def sourceAccount = UUID.randomUUID()
        def savedBookEntry = null

        accountRepo.find(bookEntry.account) >> new Account()
        accountRepo.find(sourceAccount) >> new Account()
        bookRepo.find(sourceAccount) >> book
        bookRepo.save(_ as BookEntry) >> { BookEntry b ->
            savedBookEntry = b
            savedBookEntry
        }

        expect:
        service.credit(bookEntry, sourceAccount) == savedBookEntry
    }

    def 'on credit to non-existent account, should throw exception'() {
        given:
        def bookEntry = BookEntry.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepo.find(bookEntry.account) >> null

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

        accountRepo.find(bookEntry.account) >> new Account()
        accountRepo.find(sourceAccount) >> null

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

        accountRepo.find(bookEntry.account) >> new Account()
        accountRepo.find(sourceAccount) >> new Account()
        bookRepo.find(sourceAccount) >> book

        when:
        service.credit(bookEntry, sourceAccount)

        then:
        thrown(InsufficientBalanceException)
    }
}
