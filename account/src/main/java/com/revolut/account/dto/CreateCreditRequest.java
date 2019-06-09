package com.revolut.account.dto;

import com.revolut.account.domain.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor@AllArgsConstructor
public class CreateCreditRequest {
    @NotNull
    private UUID sourceAccount;

    @Positive
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}
