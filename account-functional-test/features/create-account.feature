Feature: Create Account

    API to create a customer account

    Endpoint:
        POST {account_base_url}/accounts

    Request Body:
        {
            "customer": "{uuid}",
            "currency": "{string}",
            "balance": {decimal}
        }

  @positive_scenario
  Scenario: The user submits an API request to create an account
    Given user prepares an API request
    And request body has customer 912b3a5b-e9f1-43a5-a24f-648034a5c4ef
    And request body has currency PHP
    And request body has balance 10525.50
    When user submits a POST request to /accounts
    Then response status is 201
    And response body contains id
    And response body contains creationDate
    And response body has customer 912b3a5b-e9f1-43a5-a24f-648034a5c4ef
    And response body has currency PHP
    And response body has balance 10525.5

  @negative_scenario
  Scenario Outline: The user submits an API request to create an account but request body is invalid
    Given user prepares an API request
    And request body has customer <customer>
    And request body has currency <currency>
    When user submits a POST request to /accounts
    Then response status is 400
    And response body contains message
    And response body has code <error_code>

    Examples:
    | customer                              | currency      | error_code    |
    | 43ee460b-fccf-40ca-904f-653f218205bf  | UNSUPPORTED   | PARS000       |
    | NOT_UUID                              | PHP           | PARS000       |
    | NULL                                  | PHP           | CNST000       |
    | 152788a0-b169-4bca-a69c-a925f30bfa9c  | NULL          | CNST000       |