package com.revolut.account.repository;

import com.revolut.account.domain.Account;

import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Account find(UUID id);
}
