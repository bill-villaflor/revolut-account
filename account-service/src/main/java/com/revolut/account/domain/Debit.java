package com.revolut.account.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
public class Debit {
    private BigDecimal amount;
    private Instant date;
}
