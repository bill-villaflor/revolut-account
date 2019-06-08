package com.revolut.account.repository;

import com.revolut.account.domain.Account;

public interface AccountRepository {
    Account save(Account account);
}
