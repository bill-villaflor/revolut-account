package com.revolut.account.service;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Book;
import com.revolut.account.repository.AccountRepository;
import com.revolut.account.repository.BookRepository;
import io.micronaut.spring.tx.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class DefaultAccountService implements AccountService {
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;

    public DefaultAccountService(AccountRepository accountRepository, BookRepository bookRepository) {
        this.accountRepository = accountRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    @Override
    public Account create(Account account) {
        Account detailedAccount = account.withId(UUID.randomUUID())
                .withCreationDate(Instant.now());

        Account createdAccount = accountRepository.save(detailedAccount);

        if (hasInitialBalance(createdAccount)) {
            credit(createdAccount);
        }

        return createdAccount;
    }

    private boolean hasInitialBalance(Account account) {
        return nonNull(account.getBalance()) && account.getBalance().intValue() != 0;
    }

    private void credit(Account account) {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .credit(account.getBalance())
                .targetAccount(account.getId())
                .creationDate(account.getCreationDate())
                .build();

        bookRepository.save(book);
    }
}
