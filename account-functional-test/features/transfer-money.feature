Feature: Transfer Money

    API to transfer money from one existing account to another

    Endpoint:
        POST {account_base_url}/accounts/{id}/credits

    Request Body:
        {
            "sourceAccount": "{uuid}",
            "amount": "{decimal}",
            "currency": "{string}"
        }

    Response Body:
        {
            "referenceId": "{uuid}"
        }

  @positive_scenario
  Scenario: The user submits an API request to transfer money to another existing account
    Given user prepares an API request
    And request body has customer 9dbbcaad-d8da-40b1-814f-9e1f43598ba5
    And request body has currency PHP
    And request body has balance 0
    And user submits a POST request to /accounts
    And user keeps the id of the created resource as first_account
    And user prepares another API request
    And request body has customer 5d8afb80-c10a-4f23-b998-295c4780dcb8
    And request body has currency PHP
    And request body has balance 10000.00
    And user submits a POST request to /accounts
    And user keeps the id of the created resource as second_account
    And user prepares another API request
    And request body has sourceAccount $second_account
    And request body has amount 500.50
    And request body has currency PHP

    When user submits a POST request to /accounts/$first_account/credits

    Then response status is 201
    And response body contains referenceId
    And when user prepares another API request
    And user submits a GET request to /accounts/$first_account
    Then response body has balance 500.5
    And when user prepares another API request
    And user submits a GET request to /accounts/$second_account
    Then response body has balance 9499.5

  @positive_scenario
  Scenario: The user submits an API request to transfer money to own account
    Given user prepares an API request
    And request body has customer 9dbbcaad-d8da-40b1-814f-9e1f43598ba5
    And request body has currency PHP
    And request body has balance 500.25
    And user submits a POST request to /accounts
    And user keeps the id of the created resource as first_account
    And user prepares another API request
    And request body has sourceAccount $first_account
    And request body has amount 100
    And request body has currency PHP

    When user submits a POST request to /accounts/$first_account/credits

    Then response status is 201
    And response body contains referenceId
    And when user prepares another API request
    And user submits a GET request to /accounts/$first_account
    Then response body has balance 500.25

  @negative_scenario
  Scenario: The user submits an API request to transfer money but amount will exceed the account balance
    Given user prepares an API request
    And request body has customer 9dbbcaad-d8da-40b1-814f-9e1f43598ba5
    And request body has currency PHP
    And request body has balance 10000
    And user submits a POST request to /accounts
    And user keeps the id of the created resource as first_account
    And user prepares another API request
    And request body has sourceAccount $first_account
    And request body has amount 10001
    And request body has currency PHP

    When user submits a POST request to /accounts/$first_account/credits

    Then response status is 422
    And response body contains message
    And response body has code ACCT002

   @negative_scenario
   Scenario: The user submits an API request to transfer money from non-existent account
     Given user prepares an API request
     And request body has sourceAccount 786b92f9-532f-4355-83f1-a760e831edaf
     And request body has amount 500000
     And request body has currency PHP

     When user submits a POST request to /accounts/1d0b3175-b372-4162-8e92-d2819d96d0fc/credits

     Then response status is 404
     And response body contains message
     And response body has code ACCT000

   @negative_scenario
   Scenario: The user submits an API request to transfer money to non-existent account
     Given user prepares an API request
     And request body has customer 9dbbcaad-d8da-40b1-814f-9e1f43598ba5
     And request body has currency PHP
     And request body has balance 10500.75
     And user submits a POST request to /accounts
     And user keeps the id of the created resource as first_account
     And user prepares another API request
     And request body has sourceAccount 04248913-1948-461b-9393-1451fc19d02d
     And request body has amount 500
     And request body has currency PHP

     When user submits a POST request to /accounts/$first_account/credits

     Then response status is 422
     And response body contains message
     And response body has code ACCT001

    @negative_scenario
    Scenario Outline: The user submits an API request to transfer money with invalid request payload
      Given user prepares an API request
      And request body has sourceAccount <source_account>
      And request body has amount <amount>
      And request body has currency <currency>

      When user submits a POST request to /accounts/<target_account>/credits

      Then response status is 400
      And response body contains message
      And response body has code <error_code>

    Examples:
    | target_account                        |   source_account                        | amount  |   currency    |   error_code  |
    | INVALID                               |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | 1000.75 |   PHP         |   PARS000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   INVALID                               | 1000.75 |   PHP         |   PARS000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   NULL                                  | 1000.75 |   PHP         |   CNST000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | INVALID |   PHP         |   PARS000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | 0       |   PHP         |   CNST000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | -50000  |   PHP         |   CNST000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | NULL    |   PHP         |   CNST000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | 1000.75 |   PHPS        |   PARS000     |
    | 23ba56ac-896b-4d48-b0da-3a34c4cc1969  |   23ba56ac-896b-4d48-b0da-3a34c4cc1969  | 1000.75 |   NULL        |   CNST000     |
