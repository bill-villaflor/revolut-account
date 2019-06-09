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