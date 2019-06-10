package com.revolut.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(name="Credit", description="Credit Response")
public class CreditResponse {
    private UUID referenceId;
}
