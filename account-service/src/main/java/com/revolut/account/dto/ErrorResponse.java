package com.revolut.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name="Error", description="Error Response")
public class ErrorResponse {
    String code;
    String message;
}