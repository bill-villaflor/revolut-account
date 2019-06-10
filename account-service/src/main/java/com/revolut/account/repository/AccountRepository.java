package com.revolut.account.repository;

import com.revolut.account.domain.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Account find(UUID id);

    List<UUID> filterExisting(List<UUID> accounts);
}
