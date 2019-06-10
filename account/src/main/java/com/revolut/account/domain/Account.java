package com.revolut.account.domain;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
@Wither
public class Account {
    private UUID id;
    private UUID customer;
    private Currency currency;
    private BigDecimal balance;
    private Instant creationDate;
}