package com.revolut.account.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Wither;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Wither
public class Book {
    private UUID id;
    private BigDecimal credit;
    private BigDecimal debit;
    private Currency currency;
    private UUID account;
    private Instant creationDate;
}
