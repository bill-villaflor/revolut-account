package com.revolut.account.service;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Book;
import com.revolut.account.domain.BookEntry;
import com.revolut.account.exception.AccountNotFoundException;
import com.revolut.account.exception.InsufficientBalanceException;
import com.revolut.account.exception.SourceAccountNotFoundException;
import com.revolut.account.repository.AccountRepository;
import com.revolut.account.repository.BookRepository;
import io.micronaut.spring.tx.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DefaultAccountService implements AccountService {
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;

    public DefaultAccountService(AccountRepository accountRepository, BookRepository bookRepository) {
        this.accountRepository = accountRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    @Override
    public Account create(Account account) {
        Account detailedAccount = account.withId(UUID.randomUUID())
                .withCreationDate(Instant.now());

        Account createdAccount = accountRepository.save(detailedAccount);

        if (hasInitialBalance(createdAccount)) {
            creditInitialBalance(createdAccount);
        }

        return createdAccount;
    }

    @Transactional(readOnly = true)
    @Override
    public Account get(UUID id) {
        Account account = accountRepository.find(id);

        if (isNull(account)) {
            throw new AccountNotFoundException();
        }

        return account.withBalance(getBalance(id));
    }

    @Transactional
    @Override
    public BookEntry credit(BookEntry bookEntry, UUID sourceAccount) {
        validate(bookEntry, sourceAccount);

        BookEntry credit = bookEntry.withId(UUID.randomUUID())
                .withCreationDate(Instant.now());

        BookEntry debit = credit.withId(UUID.randomUUID())
                .withCredit(null)
                .withDebit(credit.getCredit())
                .withAccount(sourceAccount);

        bookRepository.saveAll(asList(credit, debit));

        return credit;
    }

    private boolean hasInitialBalance(Account account) {
        return nonNull(account.getBalance()) && account.getBalance().intValue() != 0;
    }

    private void creditInitialBalance(Account account) {
        BookEntry bookEntry = BookEntry.builder()
                .id(UUID.randomUUID())
                .credit(account.getBalance())
                .account(account.getId())
                .creationDate(account.getCreationDate())
                .build();

        bookRepository.save(bookEntry);
    }

    private void validate(BookEntry bookEntry, UUID sourceAccount) {
        List<UUID> accounts = asList(bookEntry.getAccount(), sourceAccount);
        List<UUID> existingAccounts = accountRepository.filterExisting(accounts);

        if (!existingAccounts.contains(bookEntry.getAccount())) {
            throw new AccountNotFoundException();
        }

        if (!existingAccounts.contains(sourceAccount)) {
            throw new SourceAccountNotFoundException();
        }

        if (hasInsufficientBalance(sourceAccount, bookEntry.getCredit())) {
            throw new InsufficientBalanceException();
        }
    }

    private boolean hasInsufficientBalance(UUID account, BigDecimal amount) {
        BigDecimal balance = getBalance(account);
        return amount.compareTo(balance) > 0;
    }

    private BigDecimal getBalance(UUID account) {
        Book book = bookRepository.find(account);
        return book.getBalance();
    }
}
