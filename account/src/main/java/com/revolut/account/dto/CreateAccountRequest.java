package com.revolut.account.dto;

import com.revolut.account.domain.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    @NotNull
    private UUID customer;

    @NotNull
    private Currency currency;

    private BigDecimal balance;
}
