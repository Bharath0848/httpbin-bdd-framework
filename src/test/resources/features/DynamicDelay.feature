Feature: HTTP Delay Endpoint Validation using HttpBin

  Background:
    Given base URL is set

Scenario: User receives delayed response based on Excel data
  Given base URL is set
  When user sends delay requests from excel "Exceldata.xlsx"
 
  # Scenario Outline: User receives delayed response based on requested wait time
  #   When user sends "<method>" request of "/delay/<seconds>"
  #   Then response status code should be equal to 200
  #   And response time should be between <min> and <max> seconds

  #   Examples:
  #     | method | seconds | min | max |
  #     | GET    | 5       | 4   | 7   |
  #     | GET    | 9       |8    | 10  |
  #     | POST   | 2       | 1   | 4   |


  Scenario Outline: Validate delay endpoint with decimal and negative values
    When user sends "<method>" request of "/delay/<value>"
    Then response status code should be equal to 200
    And response should be handled correctly for "<value>"
    Examples:
      | method | value |
      | GET    | 3.2   |
      | PUT    | 2.5   |
      | PUT    | -1    |
      | DELETE | 0     |


  Scenario Outline: Validate delay endpoint with invalid inputs
    When user sends "<method>" request of "/delay/<invalid>"
    Then response status code should be equal to 404

    Examples:
      | method | invalid |
      | POST   | abc     |
      | DELETE |         |


  Scenario: Validate incorrect delay endpoint path
    When user sends "DELETE" request of "/delaay/3"
    Then response status code should be equal to 404


  Scenario: Chaining + Schema Validation
    When user sends "POST" request with delay "2" and payload "{\"name\":\"John\",\"job\":\"QA\"}"
    Then response status code should be equal to 200
    And response should match JSON schema "Delayschema.json" 

    When user sends "PUT" request with delay "2" and payload "{\"name\":\"John\",\"job\":\"Lead\"}"
    Then response status code should be equal to 200
    And response should match previous response
  



