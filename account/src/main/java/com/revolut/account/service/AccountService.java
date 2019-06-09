package com.revolut.account.service;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Book;

import java.util.UUID;

public interface AccountService {
    Account create(Account account);

    Account get(UUID id);

    Book credit(Book book, UUID sourceAccount);
}
