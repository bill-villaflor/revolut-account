package com.revolut.account.web;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.BookEntry;
import com.revolut.account.dto.CreateCreditRequest;
import com.revolut.account.dto.CreateCreditResponse;
import com.revolut.account.service.AccountService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.UUID;

@Validated
@Controller("/accounts")
public class AccountController {
    private final AccountService service;

    @Inject
    public AccountController(AccountService service) {
        this.service = service;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Account> createAccount(@Valid @Body Account account) {
        Account createdAccount = service.create(account);
        return HttpResponse.created(createdAccount);
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Account> getAccount(UUID id) {
        Account account = service.get(id);
        return HttpResponse.ok(account);
    }

    @Post("/{id}/credits")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<CreateCreditResponse> createCredit(UUID id, @Valid @Body CreateCreditRequest request) {
        BookEntry bookEntry = BookEntry.builder()
                .credit(request.getAmount())
                .currency(request.getCurrency())
                .account(id)
                .build();

        BookEntry createdBookEntry = service.credit(bookEntry, request.getSourceAccount());

        return HttpResponse.created(new CreateCreditResponse(createdBookEntry.getId()));
    }
}
