package com.revolut.account;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Account Service",
                version = "0.1.0-SNAPSHOT",
                description = "Service to Manage Account Creation, View and Money Transfer",
                contact = @Contact(name = "Bill Villaflor", email = "bill.villaflor@gmail.com")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}