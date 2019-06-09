Feature: Get Account

    API to get a created customer account

    Endpoint:
        GET {account_base_url}/accounts/{id}

    Response Body:
        {
            "id": "{uuid}",
            "customer": "{uuid}",
            "currency": "{string}",
            "balance": {decimal},
            "creationDate": {instant}
        }

  @positive_scenario
  Scenario: The user submits an API request to get an existing account
    Given user prepares an API request
    And request body has customer b3b87770-82a3-4645-bb65-04865c3f878e
    And request body has currency PHP
    And request body has balance 200000.00
    And user submits a POST request to /accounts
    And user keeps the id of the created resource as create_account
    And user prepares another API request

    When user submits a GET request to /accounts/$create_account

    Then response status is 200
    And response body contains id
    And response body contains creationDate
    And response body has customer b3b87770-82a3-4645-bb65-04865c3f878e
    And response body has currency PHP
    And response body has balance 200000.0

  @negative_scenario
  Scenario Outline: The user submits an API request to get a non-existent account
    Given user prepares an API request
    When user submits a GET request to /accounts/<account_id>
    Then response status is <http_status>
    And response body contains message
    And response body has code <error_code>

    Examples:
    | account_id                            | error_code    |   http_status |
    | 11569bfb-fe57-4305-bb11-06012e32bf77  | ACCT000       |   404         |
    | NOT_UUID                              | PARS000       |   400         |