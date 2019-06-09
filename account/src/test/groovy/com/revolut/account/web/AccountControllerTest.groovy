package com.revolut.account.web

import com.revolut.account.domain.Account
import com.revolut.account.domain.Currency
import com.revolut.account.service.AccountService
import io.micronaut.http.HttpStatus
import spock.lang.Specification

class AccountControllerTest extends Specification {
    def service = Mock(AccountService)
    def controller = new AccountController(service)

    def account = Account.builder()
            .customer(UUID.randomUUID())
            .currency(Currency.PHP)
            .balance(200_000.50)
            .build()

    def 'on create account, should delegate call to service'() {
        given:
        service.create(account) >> account

        when:
        def response = controller.createAccount(account)

        then:
        response.body() == account
        response.status() == HttpStatus.CREATED
    }

    def 'on get account given an id, should return account from service'() {
        given:
        service.get(account.id) >> account

        when:
        def response = controller.getAccount(account.id)

        then:
        response.body() == account
        response.status() == HttpStatus.OK
    }
}
