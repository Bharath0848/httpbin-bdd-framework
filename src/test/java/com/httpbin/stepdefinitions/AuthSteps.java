package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.testng.Assert.*;

import com.httpbin.endpoints.Routes;
import com.httpbin.utils.RequestBuilder;

public class AuthSteps {

    private Response response;

    // ================= COMMON =================

    @Given("the base API is available")
    public void baseApiAvailable() {
        // Handled by Hooks
    }

    @Then("the response status should be {int}")
    public void validateStatusCode(int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
    }

    @Then("response authentication flag should be {string}")
    public void validateAuthFlag(String expected) {
        boolean expectedValue = Boolean.parseBoolean(expected);

        if (response.getStatusCode() == 200) {
            assertEquals(expectedValue, response.jsonPath().getBoolean("authenticated"));
        } else {
            // Negative case → ensure no authenticated=true
            assertFalse(response.asString().contains("\"authenticated\": true"));
        }
    }

    @Then("response should contain authenticated true")
    public void validateAuthenticatedTrue() {
        assertTrue(response.jsonPath().getBoolean("authenticated"));
    }

    @Then("response should not contain authenticated true")
    public void validateAuthenticatedFalse() {
        assertFalse(response.asString().contains("\"authenticated\": true"));
    }

    @Then("response should contain user {string}")
    public void validateUser(String user) {
        assertEquals(user, response.jsonPath().getString("user"));
    }

    @Then("response header {string} should be {string}")
    public void validateHeader(String header, String value) {
        assertEquals(value, response.getHeader(header));
    }

    @Then("response header {string} should contain {string}")
    public void validateHeaderContains(String header, String value) {
        assertTrue(response.getHeader(header).contains(value));
    }

    // ================= BASIC AUTH =================

    @When("I send Basic Auth request with username {string} and password {string}")
    public void sendBasicAuth(String username, String password) {

        // Handle empty values safely
        if (username == null) username = "";
        if (password == null) password = "";

        response = RequestBuilder.getRequest()
                .auth().preemptive().basic(username, password)
                .pathParam("user", "user")
                .pathParam("pass", "pass")
                .when()
                .get(Routes.BASIC_AUTH);
    }

    @When("I send Basic Auth request without credentials")
    public void sendBasicAuthWithoutCreds() {
        response = RequestBuilder.getRequest()
                .pathParam("user", "user")
                .pathParam("pass", "pass")
                .when()
                .get(Routes.BASIC_AUTH);
    }

    // ================= BEARER AUTH =================

    @When("I send Bearer Auth request with token {string}")
    public void sendBearerAuth(String token) {

        if (token == null || token.isEmpty()) {
            response = RequestBuilder.getRequest()
                    .when()
                    .get("/bearer");
        } else {
            response = RequestBuilder.getRequest()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/bearer");
        }
    }

    // ================= DIGEST AUTH =================

    @When("I send Digest Auth request with username {string} and password {string}")
    public void sendDigestAuth(String username, String password) {
        response = RequestBuilder.getRequest()
                .auth().digest(username, password)
                .when()
                .get("/digest-auth/auth/user/pass");
    }

    @When("I send Digest Auth request without credentials")
    public void sendDigestWithoutAuth() {
        response = RequestBuilder.getRequest()
                .when()
                .get("/digest-auth/auth/user/pass");
    }

    // ================= EDGE CASES =================

    @When("I send request with multiple Authorization headers")
    public void sendMultipleAuthHeaders() {
        response = RequestBuilder.getRequest()
                .header("Authorization", "Basic dXNlcjpwYXNz")
                .header("Authorization", "Bearer abc123token")
                .pathParam("user", "user")
                .pathParam("pass", "pass")
                .when()
                .get(Routes.BASIC_AUTH);
    }

    @When("I send request with malformed Authorization header")
    public void sendMalformedHeader() {
        response = RequestBuilder.getRequest()
                .header("Authorization", "BasicInvalidData")
                .pathParam("user", "user")
                .pathParam("pass", "pass")
                .when()
                .get(Routes.BASIC_AUTH);
    }

    @When("I send request with invalid authentication")
    public void sendInvalidAuth() {
        response = RequestBuilder.getRequest()
                .auth().preemptive().basic("user", "wrong")
                .pathParam("user", "user")
                .pathParam("pass", "pass")
                .when()
                .get(Routes.BASIC_AUTH);
    }
    
    @Then("the response status should be one of {int}, {int}")
    public void validateMultipleStatusCodes(int status1, int status2) {
        int actual = response.getStatusCode();

        if (actual != status1 && actual != status2) {
            fail("Expected " + status1 + " or " + status2 + " but got " + actual);
        }
    }
}