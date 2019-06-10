package com.revolut.account.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
public class Credit {
    private BigDecimal amount;
    private Instant date;
}
