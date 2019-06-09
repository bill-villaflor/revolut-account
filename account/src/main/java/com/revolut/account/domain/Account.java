package com.revolut.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;

    @NotNull
    private UUID customer;

    @NotNull
    private Currency currency;

    private Instant creationDate;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public void setBalance(BigDecimal balance) {
        this.balance = Objects.isNull(balance) ? BigDecimal.ZERO : balance;
    }
}