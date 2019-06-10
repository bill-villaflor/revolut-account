package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import com.revolut.account.domain.BookEntry;

import java.util.UUID;

public interface BookRepository {
    BookEntry save(BookEntry bookEntry);

    Book find(UUID account);
}
