package com.revolut.account.web

import com.revolut.account.domain.Account
import com.revolut.account.domain.Currency
import com.revolut.account.service.AccountService
import spock.lang.Specification

class AccountControllerTest extends Specification {
    def service = Mock(AccountService)
    def controller = new AccountController(service)

    def 'on create account, should delegate call to service'() {
        given:
        def account = Account.builder()
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .balance(200_000.50)
                .build()

        service.create(account) >> account

        expect:
        controller.createAccount(account).body() == account
    }
}
