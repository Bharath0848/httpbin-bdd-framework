package com.httpbin.stepdefinitions;

import com.httpbin.utils.RequestBuilder;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.junit.Assert.*;

public class RedirectSteps {

    private Response response;
    private boolean followRedirects = false;

    // ============================================================
    // BACKGROUND STEPS
    // ============================================================

    @Given("base URL is set to {string}")
    public void setBaseUrl(String baseUrl) {
        RequestBuilder.setBaseUri(baseUrl);
    }

    @Given("auto redirect is disabled")
    public void disableAutoRedirect() {
        followRedirects = false;
    }



    @When("user sends {string} request to {string}")
    public void sendRequest(String method, String endpoint) {

        switch (method.toUpperCase()) {

            case "GET":
                response = RequestBuilder.getRequest(followRedirects).get(endpoint);
                break;

            case "POST":
                response = RequestBuilder.getRequest(followRedirects).post(endpoint);
                break;

            case "PUT":
                response = RequestBuilder.getRequest(followRedirects).put(endpoint);
                break;

            case "PATCH":
                response = RequestBuilder.getRequest(followRedirects).patch(endpoint);
                break;

            case "DELETE":
                response = RequestBuilder.getRequest(followRedirects).delete(endpoint);
                break;

            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }

        
        System.out.println("====================================");
        System.out.println("Request Method: " + method);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());
        System.out.println("====================================");
    }

    // ============================================================
    // ASSERTIONS
    // ============================================================

    @Then("response status code should be {int}")
    public void validateStatusCode(int expectedStatusCode) {
        int actual = response.getStatusCode();

        assertEquals(
                "Status code mismatch! Expected: " + expectedStatusCode + " but got: " + actual,
                expectedStatusCode,
                actual
        );
    }

    @Then("response header {string} should contain {string}")
    public void validateHeaderContains(String headerName, String expectedValue) {
        String header = response.getHeader(headerName);

        assertNotNull(
                "Header '" + headerName + "' is missing in response",
                header
        );

        assertTrue(
                "Header '" + headerName + "' value mismatch! Expected to contain: "
                        + expectedValue + " but was: " + header,
                header.contains(expectedValue)
        );
    }

    @Then("response header {string} should not be null")
    public void validateHeaderNotNull(String headerName) {
        String header = response.getHeader(headerName);

        assertNotNull(
                "Header '" + headerName + "' should not be null",
                header
        );
    }

    @Then("response header {string} should be empty")
    public void validateHeaderEmpty(String headerName) {
        String header = response.getHeader(headerName);

       
        assertNotNull(
                "Header '" + headerName + "' is missing",
                header
        );

        assertTrue(
                "Header '" + headerName + "' is not empty. Actual value: " + header,
                header.trim().isEmpty()
        );
    }

    @Then("final response should not contain {string} response body")
    public void validateNoRedirectFollowed(String value) {
        String body = response.getBody().asString();

        assertFalse(
                "Redirect was followed unexpectedly. Response body contains: " + value,
                body.contains(value)
        );
    }
}