package com.revolut.account.web

import com.revolut.account.domain.Account
import com.revolut.account.domain.BookEntry
import com.revolut.account.domain.Currency
import com.revolut.account.dto.AccountResponse
import com.revolut.account.dto.CreateAccountRequest
import com.revolut.account.dto.CreateCreditRequest
import com.revolut.account.dto.CreditResponse
import com.revolut.account.service.AccountService
import io.micronaut.http.HttpStatus
import spock.lang.Specification

import java.time.Instant

class AccountControllerTest extends Specification {
    def service = Mock(AccountService)
    def controller = new AccountController(service)

    def 'on create account, should delegate call to service'() {
        given:
        def request = CreateAccountRequest.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(200_000.50)
                .build()

        def account = Account.builder()
                .customer(request.customer)
                .currency(request.currency)
                .balance(request.balance)
                .build()

        def createdAccount = account.withId(UUID.randomUUID())
                .withCreationDate(Instant.now())

        def response = AccountResponse.builder()
                .id(createdAccount.id)
                .customer(createdAccount.customer)
                .currency(createdAccount.currency)
                .balance(createdAccount.balance)
                .build()

        service.create(account) >> createdAccount

        when:
        def returnedResponse = controller.createAccount(request)

        then:
        returnedResponse.body() == response
        returnedResponse.status() == HttpStatus.CREATED
    }

    def 'on get account given an id, should return account from service'() {
        given:
        def account = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(200_000.50)
                .build()

        def response = AccountResponse.builder()
                .id(account.id)
                .customer(account.customer)
                .currency(account.currency)
                .balance(account.balance)
                .build()

        service.get(account.id) >> account

        when:
        def returnedResponse = controller.getAccount(account.id)

        then:
        returnedResponse.body() == response
        returnedResponse.status() == HttpStatus.OK
    }

    def 'on create account credit, should return reference id from service'() {
        given:
        def account = UUID.randomUUID()
        def request = CreateCreditRequest.builder()
                .sourceAccount(UUID.randomUUID())
                .currency(Currency.PHP)
                .build()

        def bookEntry = BookEntry.builder()
                .credit(request.amount)
                .currency(request.getCurrency())
                .account(account)
                .build()

        def createdBookEntry = bookEntry.withId(UUID.randomUUID())
                .withCreationDate(Instant.now())

        service.credit(bookEntry, request.sourceAccount) >> createdBookEntry

        when:
        def response = controller.createCredit(account, request)

        then:
        response.body() == new CreditResponse(createdBookEntry.id)
        response.status() == HttpStatus.CREATED
    }
}
