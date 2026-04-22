Feature: Redirect behavior validation using /redirect-to endpoint

  Background:
    Given base URL is set to "https://httpbin.org"
    And auto redirect is disabled


  # DATA TABLE SCENARIO 

  Scenario: Verify redirect for multiple relative URLs
    When user sends GET request with following URLs
      | url               |
      | /get              |
      | /status/200       |
      | /anything/test    |
    Then response status code should be 302
    And response header "Location" should contain respective URL



  # SCENARIO OUTLINE 

  Scenario Outline: Verify redirect with different query parameter combinations
    When user sends "GET" request to "/redirect-to?url=<url>"
    Then response status code should be 302
    And response header "Location" should contain "<expected>"

    Examples:
      | url                                  | expected                    |
      | /get?name=api&test=true              | /get?name=api               |
      | /get?user=bharath&id=101             | /get?user=bharath           |
      | /get?search=automation&lang=en       | /get?search=automation      |


  Scenario Outline: Verify redirect for valid absolute URL using different HTTP methods
    When user sends "<method>" request to "/redirect-to?url=<url>"
    Then response status code should be 302
    And response header "Location" should contain "<url>"

    Examples:
      | method | url                     |
      | GET    | https://example.com     |
      | POST   | https://example.com     |
      | PUT    | https://example.com     |
      | PATCH  | https://example.com     |
      | DELETE | https://example.com     |


  # POSITIVE SCENARIOS

  Scenario: Verify redirect for valid relative URL
    When user sends "GET" request to "/redirect-to?url=/get"
    Then response status code should be 302
    And response header "Location" should contain "/get"


  Scenario: Verify redirect with special characters in URL
    When user sends "GET" request to "/redirect-to?url=/get?query=hello%20world"
    Then response status code should be 302
    And response header "Location" should contain "/get?query=hello%20world"


  Scenario: Verify redirect response contains Location header
    When user sends "GET" request to "/redirect-to?url=https://example.com"
    Then response status code should be 302
    And response header "Location" should not be null

  # NEGATIVE / EDGE CASE SCENARIOS
  
  Scenario: Verify behavior when URL parameter is missing
    When user sends "GET" request to "/redirect-to"
    Then response status code should be 500


  Scenario: Verify behavior when URL is empty
    When user sends "GET" request to "/redirect-to?url="
    Then response status code should be 302
    And response header "Location" should be empty


  Scenario: Verify behavior for malformed URL
    When user sends "GET" request to "/redirect-to?url=ht!tp://invalid-url"
    Then response status code should be 302
    And response header "Location" should contain "ht!tp://invalid-url"


  Scenario: Verify redirect does not automatically follow when disabled
    When user sends "GET" request to "/redirect-to?url=/get"
    Then response status code should be 302
    And final response should not contain "/get" response body


  # CHAINING 
 

  Scenario: Verify redirect chaining with schema validation
    When user sends "GET" request to "/redirect-to?url=/get"
    Then response status code should be 302
    And user extracts "Location" header as "redirectUrl"

    When user sends "GET" request to extracted "redirectUrl"
    Then response status code should be 200
    And response body should contain "url"
    And response should match JSON schema for redirect "get_response_schema.json"