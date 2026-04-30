#Author:Mahitha
Feature: to perform CRUD operations on HttpBin StatusCode module

Background:
  Given the base url of httpbin
  
   Scenario: Validate Basic Authentication behavior
    When I send Basic Auth request with valid credentials -statuscode
    Then the auth response status should be 200


Scenario: Verify POST status with valid code
  When the post request is sent with status code 404
  Then we get the response code as 404
  And the response time is less than 4000 ms

Scenario Outline: Verify GET status with valid code
  When the get request is sent with status code <code>
  Then we get the response code as <code>
  And the response time is less than 4000 ms

Examples:
  | code |
  | 200  |

Scenario: Verify PUT status with multiple codes
  When the get request is sent with status code 200
  And the put request is sent with chained status code and below data
    | secondCode |
    | 500        |
  Then we get the response code as either chained status code or second code
  And the response time is less than 4000 ms

Scenario: Verify PATCH status with invalid code
  When the patch request is sent without status code
  Then we get the response code as 404
  And the response time is less than 4000 ms

Scenario: Verify DELETE status code with valid data
  When the delete request is sent using excel data "Sheet1"
  Then we get the response code as 204
  And the response time is less than 4000 ms