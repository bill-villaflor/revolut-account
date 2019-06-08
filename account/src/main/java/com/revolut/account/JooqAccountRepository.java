package com.revolut.account;

import org.jooq.DSLContext;

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS;
import static java.time.ZoneOffset.UTC;

public class JooqAccountRepository implements AccountRepository {
    private final DSLContext dslContext;

    public JooqAccountRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Account create(Account account) {
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, account.getId())
                .set(ACCOUNTS.CUSTOMER_ID, account.getCustomer())
                .set(ACCOUNTS.CURRENCY, account.getCurrency().toString())
                .set(ACCOUNTS.CREATION_DATE, account.getCreatedDate().atOffset(UTC))
                .execute();

        return account;
    }
}