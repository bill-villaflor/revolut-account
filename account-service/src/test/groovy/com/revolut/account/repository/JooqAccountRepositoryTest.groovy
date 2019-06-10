package com.revolut.account.repository

import com.revolut.account.domain.Account
import com.revolut.account.domain.Currency

import java.time.Instant

import static com.revolut.account.jooq.tables.Accounts.ACCOUNTS
import static java.time.ZoneOffset.UTC

class JooqAccountRepositoryTest extends JooqRepositoryTestAbstract {
    def repository = new JooqAccountRepository(create)

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

    def 'on filter existing accounts, should return only accounts that are saved in accounts table'() {
        given:
        def firstAccount = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .creationDate(Instant.now())
                .build()

        def secondAccount = Account.builder()
                .id(UUID.randomUUID())
                .customer(UUID.randomUUID())
                .currency(Currency.PHP)
                .creationDate(Instant.now())
                .build()

        insertAccount(firstAccount)
        insertAccount(secondAccount)

        when:
        def existingAccounts = repository.filterExisting([firstAccount.id, secondAccount.id, UUID.randomUUID()])

        then:
        existingAccounts.containsAll([firstAccount.id, secondAccount.id])
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
        create.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(id))
                .fetchAny()
    }

    def insertAccount(Account account) {
        create.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, account.id)
                .set(ACCOUNTS.CUSTOMER_ID, account.customer)
                .set(ACCOUNTS.CURRENCY, account.currency.toString())
                .set(ACCOUNTS.CREATION_DATE, account.getCreationDate().atOffset(UTC))
                .execute()
    }
}
