package com.revolut.account.repository;

import com.revolut.account.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BookRepository {
    Book save(Book book);

    List<BigDecimal> findCredits(UUID account);

    List<BigDecimal> findDebits(UUID account);
}
