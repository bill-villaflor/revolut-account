package com.revolut.account.dto;

import com.revolut.account.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
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
@NoArgsConstructor
@AllArgsConstructor
@Schema(name="CreateCredit", description="Create Credit Request")
public class CreateCreditRequest {
    @NotNull
    private UUID sourceAccount;

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}
