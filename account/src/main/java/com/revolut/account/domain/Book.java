package com.revolut.account.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Book {
    private UUID id;
    private BigDecimal credit;
    private BigDecimal debit;
    private UUID sourceAccount;
    private UUID targetAccount;
    private Instant creationDate;
}
