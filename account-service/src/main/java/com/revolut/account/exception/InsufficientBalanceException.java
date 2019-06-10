package com.revolut.account.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Your current balance is not enough to credit another account.");
    }
}
