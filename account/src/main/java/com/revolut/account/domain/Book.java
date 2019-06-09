package com.revolut.account.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class Book {
    private List<Credit> credits;
    private List<Debit> debits;

    public BigDecimal getBalance() {
        BigDecimal credit = credits.stream()
                .map(Credit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debit = debits.stream()
                .map(Debit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return credit.subtract(debit);
    }
}