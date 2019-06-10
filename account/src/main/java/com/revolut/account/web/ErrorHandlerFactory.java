package com.revolut.account.web;

import com.revolut.account.exception.AccountNotFoundException;
import com.revolut.account.exception.ErrorResponse;
import com.revolut.account.exception.InsufficientBalanceException;
import com.revolut.account.exception.SourceAccountNotFoundException;
import io.micronaut.context.annotation.Factory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.serverError;
import static io.micronaut.http.HttpResponse.unprocessableEntity;

@Factory
public class ErrorHandlerFactory {
    @Singleton
    public ExceptionHandler<InsufficientBalanceException, HttpResponse> insufficientFundHandler() {
        return (request, e) -> unprocessableEntity().body(error(
                "ACCT002",
                e.getMessage()));
    }

    @Singleton
    public ExceptionHandler<SourceAccountNotFoundException, HttpResponse> sourceAccountNotFoundHandler() {
        return (request, e) -> unprocessableEntity().body(error(
                "ACCT001",
                e.getMessage()));
    }

    @Singleton
    public ExceptionHandler<AccountNotFoundException, HttpResponse> accountNotFoundHandler() {
        return (request, e) -> notFound(error(
                "ACCT000",
                e.getMessage()));
    }

    @Singleton
    public ExceptionHandler<Exception, HttpResponse> genericErrorHandler() {
        return (request, e) -> serverError(error(
                "SERV000",
                "An application error has occurred. Please try again later."
        ));
    }

    private ErrorResponse error(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}