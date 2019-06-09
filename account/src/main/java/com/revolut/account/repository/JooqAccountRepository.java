package com.revolut.account.repository;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Currency;
import com.revolut.account.jooq.tables.records.AccountsRecord;
import org.jooq.DSLContext;

import java.util.Optional;
import java.util.UUID;

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS;
import static java.time.ZoneOffset.UTC;

public class JooqAccountRepository implements AccountRepository {
    private final DSLContext dslContext;

    public JooqAccountRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Account save(Account account) {
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, account.getId())
                .set(ACCOUNTS.CUSTOMER_ID, account.getCustomer())
                .set(ACCOUNTS.CURRENCY, account.getCurrency().toString())
                .set(ACCOUNTS.CREATION_DATE, account.getCreationDate().atOffset(UTC))
                .execute();

        return account;
    }

    @Override
    public Account find(UUID id) {
        return Optional.ofNullable(dslContext.fetchAny(ACCOUNTS, ACCOUNTS.ID.eq(id)))
                .map(this::toAccount)
                .orElse(null);
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