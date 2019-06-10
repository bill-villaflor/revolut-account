package com.revolut.account.repository;

import io.micronaut.context.annotation.Factory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class RepositoryFactory {
    private final DataSource dataSource;

    @Inject
    public RepositoryFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Singleton
    DSLContext dslContext() {
        return new DefaultDSLContext(dataSource, SQLDialect.H2);
    }

    @Singleton
    AccountRepository accountRepository() {
        return new JooqAccountRepository(dslContext());
    }

    @Singleton
    BookRepository bookRepository() {
        return new JooqBookRepository(dslContext());
    }
}
