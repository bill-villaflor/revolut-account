package com.revolut.account;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class Book {
    private UUID id;
    private BigDecimal credit;
    private BigDecimal debit;
    private UUID sourceAccount;
    private UUID targetAccount;
    private Instant creationDate;
}
