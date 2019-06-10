package com.revolut.account.web;

import com.revolut.account.domain.Account;
import com.revolut.account.domain.BookEntry;
import com.revolut.account.dto.AccountResponse;
import com.revolut.account.dto.CreateAccountRequest;
import com.revolut.account.dto.CreateCreditRequest;
import com.revolut.account.dto.CreditResponse;
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
@Controller("/v1/accounts")
public class AccountController {
    private final AccountService service;

    @Inject
    public AccountController(AccountService service) {
        this.service = service;
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<AccountResponse> createAccount(@Valid @Body CreateAccountRequest request) {
        Account account = Account.builder()
                .customer(request.getCustomer())
                .currency(request.getCurrency())
                .balance(request.getBalance())
                .build();

        Account createdAccount = service.create(account);
        return HttpResponse.created(body(createdAccount));
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<AccountResponse> getAccount(UUID id) {
        Account account = service.get(id);
        return HttpResponse.ok(body(account));
    }

    @Post("/{id}/credits")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<CreditResponse> createCredit(UUID id, @Valid @Body CreateCreditRequest request) {
        BookEntry bookEntry = BookEntry.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .source(request.getSourceAccount())
                .destination(id)
                .build();

        BookEntry createdBookEntry = service.credit(bookEntry);
        return HttpResponse.created(new CreditResponse(createdBookEntry.getId()));
    }

    private AccountResponse body(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .customer(account.getCustomer())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .build();
    }
}
