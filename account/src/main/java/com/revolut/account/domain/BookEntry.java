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
public class BookEntry {
    private UUID id;
    private BigDecimal amount;
    private Currency currency;
    private UUID source;
    private UUID destination;
    private Instant creationDate;
}
