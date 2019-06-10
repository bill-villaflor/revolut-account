package com.revolut.account.domain

import spock.lang.Specification

import java.time.Instant

class BookTest extends Specification {
    def 'on get balance, should compute balance from credits and debits'() {
        given:
        def credits = [
                new Credit(10000.00, Instant.now()),
                new Credit(1000.50, Instant.now()),
                new Credit(500.50, Instant.now())
        ]

        def debits = [
                new Debit(250.00, Instant.now()),
                new Debit(250.00, Instant.now())
        ]

        def book = new Book(credits, debits)

        expect:
        book.getBalance() == 11001.00
    }
}