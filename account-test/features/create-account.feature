Feature: Create Account

    API to create a customer account

    Endpoint:
        POST {account_base_url}/v1/accounts

    Request Body:
        {
            "customer": "{uuid}",
            "currency": "{string}",
            "balance": {decimal}
        }

    Response Body:
        {
            "id": "{uuid}",
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

    When user submits a POST request to /v1/accounts

    Then response status is 201
    And response body contains id
    And response body has customer 912b3a5b-e9f1-43a5-a24f-648034a5c4ef
    And response body has currency PHP
    And response body has balance 10525.5

  @negative_scenario
  Scenario Outline: The user submits an API request to create an account but request body is invalid
    Given user prepares an API request
    And request body has customer <customer>
    And request body has currency <currency>

    When user submits a POST request to /v1/accounts

    Then response status is 400

    Examples:
    | customer                              | currency      |
    | 43ee460b-fccf-40ca-904f-653f218205bf  | UNSUPPORTED   |
    | NOT_UUID                              | PHP           |
    | NULL                                  | PHP           |
    | 152788a0-b169-4bca-a69c-a925f30bfa9c  | NULL          |