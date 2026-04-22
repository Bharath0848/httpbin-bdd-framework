#Author:Mahitha
Feature: to perform CRUD operations on HttpBin StatusCode module

Background:
  Given the base url of httpbin

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
  Then we get the response code as either chained status code or 500
  And the response time is less than 4000 ms

Scenario: Verify PATCH status with invalid code
  When the patch request is sent without status code
  Then we get the response code as 404
  And the response time is less than 4000 ms

Scenario: Verify DELETE status with no content code
  When the delete request is sent with status code 204
  Then we get the response code as 204
  And the response time is less than 4000 ms