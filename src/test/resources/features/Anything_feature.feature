Feature: Anything Module Full CRUD Operations
  As a user, I want to manage records in the Anything module 
  So that I can create, read, update, and delete data securely.

  Background:
    Given I have the API base URL "https://httpbin.org"

  Scenario Outline: User creates a new record and saves the ID
    And I provide user details with id <id>, name "<name>", and status "<active>"
    When I submit a request to create a record
    Then the request should be successful with status 200
    And I save the unique ID from the response for future use

    Examples:
      | id   | name           | active |
      | 994  | Madhan_User    | true   |
      | 105  | Arun_Tester    | false  |

  Scenario: User views record details with security and tracking
    Given I log in with valid credentials "user" and "pass"
    And I include a tracking ID "REQ-001" and a timestamp
    When I request the record details
    Then the request should be successful with status 200
    And the response should display the correct tracking ID

  Scenario: User updates all information for a record
    And I update the status to "Updated_Batch5"
    When I submit the update request
    Then the request should be successful with status 200
    And the record status should show as "Updated_Batch5"

  Scenario: User updates only the age of a record
    And I change the age to 28
    When I submit the partial update request
    Then the request should be successful with status 200
    And the record age should show as 28

  Scenario: User deletes a record using the saved ID
    When I delete the record using the previously saved ID
    Then the request should be successful with status 200
    And the system should confirm the correct ID was removed

  Scenario: User verifies system behavior for invalid requests
    When I try to access a non-existent page "/anythin"
    Then I should receive a 404 Not Found error
    When I use the wrong method for the status page
    Then I should receive a 405 Method Not Allowed error
    And the response should list the allowed methods