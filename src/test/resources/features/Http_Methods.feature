Feature: Validate HTTPBin API Operations

Scenario: Verify GET echoes query parameters
  Given I set base URL
  When I send GET request with valid query params
  Then I validate GET response

Scenario: Verify POST echoes request body
  Given I set base URL
  When I send POST request with valid JSON
  Then I validate POST response

Scenario: Verify PUT echoes updated data
  Given I set base URL
  When I send PUT request with valid data
  Then I validate PUT response

Scenario: Verify PATCH with invalid format (Negative)
  Given I set base URL
  When I send PATCH request with invalid data
  Then I validate PATCH response


Scenario: Verify DELETE with parameters
  Given I set base URL
  When I send DELETE request with valid params
  Then I validate DELETE response