package com.revolut.account.web;

import com.revolut.account.domain.Account;
import com.revolut.account.service.AccountService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import javax.inject.Inject;

@Controller("/accounts")
public class AccountController {
    private final AccountService service;

    @Inject
    public AccountController(AccountService service) {
        this.service = service;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(Account account) {
        return service.create(account);
    }
}
