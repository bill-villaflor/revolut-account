package com.revolut.account.service;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.BookEntry;

import java.util.UUID;

public interface AccountService {
    Account create(Account account);

    Account get(UUID id);

    BookEntry credit(BookEntry bookEntry, UUID sourceAccount);
}
