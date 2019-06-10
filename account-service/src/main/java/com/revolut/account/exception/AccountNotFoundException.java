package com.revolut.account.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account is not found. Please use an existing account id.");
    }
}
