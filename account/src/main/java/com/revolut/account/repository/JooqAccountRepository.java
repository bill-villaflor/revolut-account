package com.revolut.account.repository;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Currency;
import com.revolut.account.jooq.tables.records.AccountsRecord;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

public class JooqAccountRepository implements AccountRepository {
    private final DSLContext create;

    public JooqAccountRepository(DSLContext create) {
        this.create = create;
    }

    @Override
    public Account save(Account account) {
        AccountsRecord record = create.newRecord(ACCOUNTS);
        record.setId(account.getId());
        record.setCustomerId(account.getCustomer());
        record.setCurrency(account.getCurrency().toString());
        record.setCreationDate(account.getCreationDate().atOffset(UTC));

        record.store();

        return account;
    }

    @Override
    public Account find(UUID id) {
        return Optional.ofNullable(create.fetchAny(ACCOUNTS, ACCOUNTS.ID.eq(id)))
                .map(this::toAccount)
                .orElse(null);
    }

    @Override
    public List<UUID> filterExisting(List<UUID> accounts) {
        return create.select(ACCOUNTS.ID)
                .from(ACCOUNTS)
                .where(ACCOUNTS.ID.in(accounts))
                .stream()
                .map(record -> record.get(ACCOUNTS.ID))
                .collect(toList());
    }

    private Account toAccount(AccountsRecord record) {
        return Account.builder()
                .id(record.getId())
                .customer(record.getCustomerId())
                .currency(Currency.valueOf(record.getCurrency()))
                .creationDate(record.getCreationDate().toInstant())
                .build();
    }
}