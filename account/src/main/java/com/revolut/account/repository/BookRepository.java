package com.revolut.account.repository;

import com.revolut.account.domain.Book;

public interface BookRepository {
    Book save(Book book);
}
