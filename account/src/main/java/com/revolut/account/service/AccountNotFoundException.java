package com.revolut.account.service;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account is not found. Please use an existing account id.");
    }
}
