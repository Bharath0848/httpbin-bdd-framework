Feature: HTTP Delay Endpoint Validation using HttpBin

  Background:
    Given base URL is set


 
  Scenario Outline: User receives delayed response based on requested wait time
    When user sends "<method>" request to "/delay/<seconds>"
    Then response status code should be 200
    And response time should be between <min> and <max> seconds
    And response should match JSON schema "delaySchema.json"

    Examples:
      | method | seconds | min | max |
      | GET    | 5       | 4   | 7   |
      | GET    | 9       |8    | 10  |
      | POST   | 2       | 1   | 4   |


  Scenario Outline: Validate delay endpoint with decimal and negative values
    When user sends "<method>" request to "/delay/<value>"
    Then response status code should be 200
    And response should be handled correctly for "<value>"
    And response should match JSON schema "delaySchema.json"

    Examples:
      | method | value |
      | GET    | 3.2   |
      | PUT    | 2.5   |
      | PUT    | -1    |
      | DELETE | 0     |


  Scenario Outline: Validate delay endpoint with invalid inputs
    When user sends "<method>" request to "/delay/<invalid>"
    Then response status code should be 404

    Examples:
      | method | invalid |
      | POST   | abc     |
      | DELETE |         |


  Scenario: Validate incorrect delay endpoint path
    When user sends "DELETE" request to "/delaay/3"
    Then response status code should be 404



