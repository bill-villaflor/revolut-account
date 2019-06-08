package com.revolut.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    private UUID customer;
    private Currency currency;
    private Instant creationDate;
    private BigDecimal balance;
}