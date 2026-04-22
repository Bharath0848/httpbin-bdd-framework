Feature: Validate HTTP methods behavior using httpbin

Background:
  Given HTTPBin base URL is set to "https://httpbin.org"


Scenario Outline: Validate GET request with different inputs
  When user sends GET request to "/get?name=<name>&role=<role>"
  Then response args will have name "<name>" and role "<role>"
  And response time will be less than 3000 ms

Examples:
  | name  | role   |
  | arthi | tester |
  | john  | dev    |


Scenario: Validate POST request payload using DataTable
  When user sends POST request with below data
    | name  | role   |
    | arthi | tester |
  Then response json will match sent payload
  And response time will be less than 3000 ms


Scenario: Validate PUT request updates data
  When user sends PUT request to "/put" with name "arthi" and role "lead"
  Then response json will reflect role "lead"
  And response time will be less than 3000 ms


Scenario: Validate PATCH request with invalid input
  When user sends PATCH request to "/patch" with invalid data
  Then response will handle invalid input
  And response time will be less than 3000 ms


Scenario: Validate DELETE request parameters
  When user sends DELETE request to "/delete?name=arthi&role=tester"
  Then response args will contain "arthi" and "tester"
  And response time will be less than 3000 ms