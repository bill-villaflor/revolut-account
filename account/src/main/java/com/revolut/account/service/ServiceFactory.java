package com.revolut.account.service;

import com.revolut.account.repository.AccountRepository;
import com.revolut.account.repository.BookRepository;
import io.micronaut.context.annotation.Factory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Factory
public class ServiceFactory {
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;

    @Inject
    public ServiceFactory(AccountRepository accountRepository, BookRepository bookRepository) {
        this.accountRepository = accountRepository;
        this.bookRepository = bookRepository;
    }

    @Singleton
    AccountService accountService() {
        return new DefaultAccountService(accountRepository, bookRepository);
    }
}
