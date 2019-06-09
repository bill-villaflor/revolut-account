package com.revolut.account.repository

import com.revolut.account.domain.Account
import com.revolut.account.domain.Currency

import java.time.Instant

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS
import static java.time.ZoneOffset.UTC

class JooqAccountRepositoryTest extends JooqRepositoryTestAbstract {
    def repository = new JooqAccountRepository(dslContext)

    def 'on save account, should store details to accounts table'() {
        given:
        def account = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .creationDate(Instant.now())
                .build()

        when:
        def savedAccount = repository.save(account)

        then:
        savedAccount == account
        assertAccountIsInAccountsTable(account)
    }

    def 'on find account, should return saved account from accounts table'() {
        given:
        def account = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .creationDate(Instant.now())
                .build()

        insertAccount(account)

        expect:
        repository.find(account.id) == account
    }

    def 'on find missing account, should return null'() {
        expect:
        !repository.find(UUID.randomUUID())
    }

    void assertAccountIsInAccountsTable(Account account) {
        def savedAccount = selectAccount(account.id)

        assert savedAccount
        assert savedAccount.id == account.id
        assert savedAccount.customerId == account.customer
        assert savedAccount.currency == account.currency.toString()
        assert savedAccount.creationDate.toInstant() == account.creationDate
    }

    def selectAccount(UUID id) {
        dslContext.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(id))
                .fetchAny()
    }

    def insertAccount(Account account) {
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, account.id)
                .set(ACCOUNTS.CUSTOMER_ID, account.customer)
                .set(ACCOUNTS.CURRENCY, account.currency.toString())
                .set(ACCOUNTS.CREATION_DATE, account.getCreationDate().atOffset(UTC))
                .execute()
    }
}