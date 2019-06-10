package com.revolut.account.dto;

import com.revolut.account.domain.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class AccountResponse {
    private UUID id;
    private UUID customer;
    private Currency currency;
    private BigDecimal balance;
}