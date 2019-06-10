package com.revolut.account.dto;

import com.revolut.account.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name="CreateAccount", description="Create Account Request")
public class CreateAccountRequest {
    @NotNull
    private UUID customer;

    @NotNull
    private Currency currency;

    private BigDecimal balance;
}
