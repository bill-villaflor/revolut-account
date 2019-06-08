package com.revolut.account


import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.h2.jdbcx.JdbcDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DefaultDSLContext
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS

class JooqAccountRepositoryTest extends Specification {
    @Shared
    def dataSource = new JdbcDataSource(
            url: 'jdbc:h2:mem:account_test_db;DB_CLOSE_DELAY=-1',
            user: 'sa',
            password: ''
    )

    @Shared
    def flyway = new Flyway(new ClassicConfiguration(dataSource: dataSource))

    @Shared
    def dslContext = new DefaultDSLContext(dataSource, SQLDialect.H2)

    @Shared
    def repository = new JooqAccountRepository(dslContext)

    def setupSpec() {
        flyway.migrate()
    }

    def 'on account creation, should store details to accounts table'() {
        given:
        def account = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .createdDate(Instant.now())
                .build()

        when:
        def createdAccount = repository.create(account)

        then:
        createdAccount == account
        assertAccountIsStoredInDb(account)
    }

    void assertAccountIsStoredInDb(Account account) {
        def savedAccount = selectAccount(account.id)

        assert savedAccount
        assert account.id == savedAccount.id
        assert account.customer == savedAccount.customerId
        assert account.currency.toString() == savedAccount.currency
        assert account.createdDate == savedAccount.creationDate.toInstant()
    }

    def selectAccount(UUID id) {
        dslContext.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(id))
                .fetchAny()
    }
}
