package com.revolut.account.repository;

import com.revolut.account.domain.Book;
import com.revolut.account.domain.BookEntry;

import java.util.List;
import java.util.UUID;

public interface BookRepository {
    BookEntry save(BookEntry bookEntry);

    List<BookEntry> saveAll(List<BookEntry> bookEntries);

    Book find(UUID account);
}
