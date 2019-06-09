package com.revolut.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CreateCreditResponse {
    private UUID referenceId;
}
