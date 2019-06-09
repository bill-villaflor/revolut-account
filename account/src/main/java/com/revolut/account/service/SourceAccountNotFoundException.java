package com.revolut.account.service;

public class SourceAccountNotFoundException extends RuntimeException {
    public SourceAccountNotFoundException() {
        super("Source account to be debited is not found. Please use an existing account id.");
    }
}
