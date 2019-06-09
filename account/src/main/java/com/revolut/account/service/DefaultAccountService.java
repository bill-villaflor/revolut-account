package com.revolut.account.service;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.Book;
import com.revolut.account.repository.AccountRepository;
import com.revolut.account.repository.BookRepository;
import io.micronaut.spring.tx.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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
            credit(createdAccount);
        }

        return createdAccount;
    }

    @Transactional(readOnly = true)
    @Override
    public Account get(UUID id) {
        Account account = accountRepository.find(id);

        if (Objects.isNull(account)) {
            throw new AccountNotFoundException();
        }

        return account.withBalance(balance(id));
    }

    @Transactional
    @Override
    public Book credit(Book book, UUID sourceAccount) {
        if (Objects.isNull(accountRepository.find(book.getAccount()))) {
            throw new AccountNotFoundException();
        }

        if (Objects.isNull(accountRepository.find(sourceAccount))) {
            throw new SourceAccountNotFoundException();
        }

        Book creditBook = book.withId(UUID.randomUUID())
                .withCreationDate(Instant.now());

        Book debitBook = creditBook.withId(UUID.randomUUID())
                .withCredit(null)
                .withDebit(creditBook.getCredit())
                .withAccount(sourceAccount);

        bookRepository.save(debitBook);

        return bookRepository.save(creditBook);
    }

    private boolean hasInitialBalance(Account account) {
        return nonNull(account.getBalance()) && account.getBalance().intValue() != 0;
    }

    private void credit(Account account) {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .credit(account.getBalance())
                .account(account.getId())
                .creationDate(account.getCreationDate())
                .build();

        bookRepository.save(book);
    }

    private BigDecimal balance(UUID account) {
        BigDecimal credit = bookRepository.findCredits(account).stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debit = bookRepository.findDebits(account).stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return credit.subtract(debit);
    }
}
