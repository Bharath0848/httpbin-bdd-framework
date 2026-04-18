
Feature: to perform CRUD operations on HttpBin StatusCode module
Background:
Given the base url of httpbin

Scenario: Verify POST status with valid code
When the post request is sent with status code 404
Then we get the response code as 404
And the response time is less than 3000 ms

Scenario: Verify GET status with valid code
When the get request is sent with status code 200
Then we get the response code as 200
And the response time is less than 3000 ms

Scenario: Verify PUT status with multiple codes
When the put request is sent with status codes "200,500"
Then we get the response code as either 200 or 500
And the response time is less than 3000 ms

Scenario: Verify PATCH status with invalid code
When the patch request is sent without status code
Then we get the response code as 404
And the response time is less than 3000 ms

Scenario: Verify DELETE status with no content code
When the delete request is sent with status code 204
Then we get the response code as 204
And the response time is less than 3000 ms