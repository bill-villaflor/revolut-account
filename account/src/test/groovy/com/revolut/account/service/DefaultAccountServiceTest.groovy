package com.revolut.account.service

import com.revolut.account.domain.Account
import com.revolut.account.domain.Book
import com.revolut.account.domain.Currency
import com.revolut.account.exception.AccountNotFoundException
import com.revolut.account.exception.InsufficientBalanceException
import com.revolut.account.exception.SourceAccountNotFoundException
import com.revolut.account.repository.AccountRepository
import com.revolut.account.repository.BookRepository
import spock.lang.Specification

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
        def savedBook = Book.builder().build()

        accountRepo.save(_ as Account) >> { Account a ->
            savedAccount = a
            savedAccount
        }

        bookRepo.save(_ as Book) >> { Book b ->
            savedBook = b
            savedBook
        }

        when:
        def returnedAccount = service.create(account)

        then:
        returnedAccount == savedAccount
        savedBook.id
        savedBook.credit == savedAccount.balance
        !savedBook.debit
        savedBook.account == savedAccount.id
        savedBook.creationDate == savedAccount.creationDate
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

        def credits = [10_000.00, 525.50]
        def debits = [500.00, 25.50]
        def balance = 10_000.00

        accountRepo.find(account.id) >> account
        bookRepo.findCredits(account.id) >> credits
        bookRepo.findDebits(account.id) >> debits

        when:
        def returnedAccount = service.get(account.id)

        then:
        returnedAccount == account.withBalance(balance)
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
        def book = Book.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()
        def savedBook = null

        accountRepo.find(book.account) >> new Account()
        accountRepo.find(sourceAccount) >> new Account()
        bookRepo.findDebits(sourceAccount) >> []
        bookRepo.findCredits(sourceAccount) >> [10_000.50]
        bookRepo.save(_ as Book) >> { Book b ->
            savedBook = b
            savedBook
        }

        when:
        def createdBook = service.credit(book, sourceAccount)

        then:
        createdBook == savedBook
    }

    def 'on credit to non-existent account, should throw exception'() {
        given:
        def book = Book.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepo.find(book.account) >> null

        when:
        service.credit(book, sourceAccount)

        then:
        thrown(AccountNotFoundException)
    }

    def 'on credit from non-existent account, should throw exception'() {
        given:
        def book = Book.builder()
                .credit(10_000.50)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepo.find(book.account) >> new Account()
        accountRepo.find(sourceAccount) >> null

        when:
        service.credit(book, sourceAccount)

        then:
        thrown(SourceAccountNotFoundException)
    }

    def 'on credit from account with insufficient balance, should throw exception'() {
        given:
        def book = Book.builder()
                .credit(500.00)
                .currency(Currency.PHP)
                .account(UUID.randomUUID())
                .build()

        def sourceAccount = UUID.randomUUID()

        accountRepo.find(book.account) >> new Account()
        accountRepo.find(sourceAccount) >> new Account()
        bookRepo.findDebits(sourceAccount) >> []
        bookRepo.findCredits(sourceAccount) >> []

        when:
        service.credit(book, sourceAccount)

        then:
        thrown(InsufficientBalanceException)
    }
}
