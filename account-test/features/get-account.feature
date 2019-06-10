Feature: Get Account

    API to get a created customer account

    Endpoint:
        GET {account_base_url}/v1/accounts/{id}

    Response Body:
        {
            "id": "{uuid}",
            "customer": "{uuid}",
            "currency": "{string}",
            "balance": {decimal}
        }

  @positive_scenario
  Scenario: The user submits an API request to get an existing account
    Given user prepares an API request
    And request body has customer b3b87770-82a3-4645-bb65-04865c3f878e
    And request body has currency PHP
    And request body has balance 200000.00
    And user submits a POST request to /v1/accounts
    And user keeps the id of the created resource as create_account
    And user prepares another API request

    When user submits a GET request to /v1/accounts/$create_account

    Then response status is 200
    And response body contains id
    And response body has customer b3b87770-82a3-4645-bb65-04865c3f878e
    And response body has currency PHP
    And response body has balance 200000.0

  @negative_scenario
  Scenario: The user submits an API request to get a non-existent account
    Given user prepares an API request
    When user submits a GET request to /v1/accounts/11569bfb-fe57-4305-bb11-06012e32bf77
    Then response status is 404
    And response body contains message
    And response body has code ACCT000

  @negative_scenario
  Scenario: The user submits an API request to get an account with invalid id
    Given user prepares an API request
    When user submits a GET request to /v1/accounts/INVALID
    Then response status is 400