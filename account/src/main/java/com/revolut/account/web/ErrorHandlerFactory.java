package com.revolut.account.web;

import com.revolut.account.service.AccountNotFoundException;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;

import static io.micronaut.http.HttpResponse.badRequest;
import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.serverError;

@Factory
public class ErrorHandlerFactory {
    @Singleton
    public ExceptionHandler<AccountNotFoundException, HttpResponse> accountNotFoundHandler() {
        return (request, e) -> notFound(error(
                "ACCT000",
                e.getMessage()));
    }

    @Singleton
    public ExceptionHandler<ConstraintViolationException, HttpResponse> constraintErrorHandler() {
        return (request, e) -> badRequest(error(
                "CNST000",
                "A field constraint is violated. Please refer to the API docs."
        ));
    }

    @Singleton
    public ExceptionHandler<ConversionErrorException, HttpResponse> conversionErrorHandler() {
        return (request, e) -> badRequest(error(
                "PARS000",
                "Can't read one of the request params. Please refer to the API docs."
        ));
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
