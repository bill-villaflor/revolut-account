package com.revolut.account.web;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;

@Factory
public class ErrorHandlerFactory {
    @Produces
    @Singleton
    @Requires(classes = {ConstraintViolationException.class, ExceptionHandler.class})
    public ExceptionHandler<ConstraintViolationException, HttpResponse> constraintErrorHandler() {
        ErrorResponse error = ErrorResponse.builder()
                .code("CNST000")
                .message("A field constraint is violated. Please refer to the API docs.")
                .build();

        return (request, e) -> HttpResponse.badRequest(error);
    }

    @Produces
    @Singleton
    @Requires(classes = {ConversionErrorException.class, ExceptionHandler.class})
    public ExceptionHandler<ConversionErrorException, HttpResponse> conversionErrorHandler() {
        ErrorResponse error = ErrorResponse.builder()
                .code("PARS000")
                .message("Can't read one of the request params. Please refer to the API docs.")
                .build();

        return (request, e) -> HttpResponse.badRequest(error);
    }

    @Produces
    @Singleton
    @Requires(classes = {Exception.class, ExceptionHandler.class})
    public ExceptionHandler<Exception, HttpResponse> genericErrorHandler() {
        ErrorResponse error = ErrorResponse.builder()
                .code("SERV000")
                .message("An application error has occurred. Please try again later.")
                .build();

        return (request, e) -> HttpResponse.serverError(error);
    }
}
