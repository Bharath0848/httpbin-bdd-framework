Feature: Authentication Module Validation

  Background:
    Given the base API is available

  # ================= BASIC AUTH =================

  Scenario Outline: Validate Basic Authentication behavior
    When I send Basic Auth request with username "<username>" and password "<password>"
    Then the response status should be <status>
    And response authentication flag should be "<authFlag>"

    Examples:
      | username | password | status | authFlag |
      | user     | pass     | 200    | true     |
      | user     | wrong    | 401    | false    |
      | user     |          | 401    | false    |
      |          | pass     | 401    | false    |

  # TC_AUTH_11
  Scenario: Validate Basic Auth without Authorization header
    When I send Basic Auth request without credentials
    Then the response status should be 401

  # TC_AUTH_04, TC_AUTH_05
  Scenario: Validate Basic Auth response structure and headers
    When I send Basic Auth request with username "user" and password "pass"
    Then the response status should be 200
    And response header "Content-Type" should be "application/json"
    And response should contain authenticated true
    And response should contain user "user"

  # ================= BEARER AUTH =================

  Scenario Outline: Validate Bearer Authentication behavior
    When I send Bearer Auth request with token "<token>"
    Then the response status should be <status>

    Examples:
      | token        | status |
      | abc123token  | 200    |
      |              | 401    |

  # ================= DIGEST AUTH =================

  Scenario Outline: Validate Digest Authentication behavior
    When I send Digest Auth request with username "<username>" and password "<password>"
    Then the response status should be <status>

    Examples:
      | username | password | status |
      | user     | pass     | 200    |
      | user     | wrong    | 401    |

  # TC_AUTH_13
  Scenario: Validate Digest Auth challenge response
    When I send Digest Auth request without credentials
    Then the response status should be 401
    And response header "WWW-Authenticate" should contain "Digest"

  # ================= EDGE CASES =================

  Scenario: Validate multiple Authorization headers handling
    When I send request with multiple Authorization headers
    Then the response status should be one of 400, 401

  Scenario: Validate malformed Authorization header
    When I send request with malformed Authorization header
    Then the response status should be one of 400, 401

  # TC_AUTH_14
  Scenario: Validate failed authentication response structure
    When I send request with invalid authentication
    Then the response status should be 401
    And response should not contain authenticated true