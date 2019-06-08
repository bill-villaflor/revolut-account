package com.revolut.account.service

import com.revolut.account.domain.Account
import com.revolut.account.domain.Book
import com.revolut.account.domain.Currency
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
        savedBook.targetAccount == savedAccount.id
        !savedBook.sourceAccount
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
}
