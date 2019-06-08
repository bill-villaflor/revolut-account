package com.revolut.account;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class Account {
    private UUID id;
    private UUID customer;
    private Currency currency;
    private Instant createdDate;
}