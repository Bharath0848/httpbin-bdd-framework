package com.httpbin.stepdefinitions;

import com.httpbin.utils.RequestBuilder;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

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

    // ============================================================
    // REQUEST HANDLING
    // ============================================================

    @When("user sends {string} request to {string}")
    public void sendRequest(String method, String endpoint) {

        RequestSpecification request =
                RequestBuilder.getRequest(followRedirects);

        switch (method.toUpperCase()) {

            case "GET":
                response = request.get(endpoint);
                break;

            case "POST":
                response = request.post(endpoint);
                break;

            case "PUT":
                response = request.put(endpoint);
                break;

            case "PATCH":
                response = request.patch(endpoint);
                break;

            case "DELETE":
                response = request.delete(endpoint);
                break;

            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }

        // Logging
        System.out.println("====================================");
        System.out.println("Request Method: " + method);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("====================================");
    }

    // ============================================================
    // ASSERTIONS
    // ============================================================

    @Then("response status code should be {int}")
    public void validateStatusCode(int expectedStatusCode) {

        Assert.assertNotNull(response, "Response is null");

        int actual = response.getStatusCode();

        Assert.assertEquals(
                actual,
                expectedStatusCode,
                "Status code mismatch! Expected: " + expectedStatusCode + " but got: " + actual
        );
    }

    @Then("response header {string} should contain {string}")
    public void validateHeaderContains(String headerName, String expectedValue) {

        String header = response.getHeader(headerName);

        Assert.assertNotNull(
                header,
                "Header '" + headerName + "' is missing in response"
        );

        Assert.assertTrue(
                header.contains(expectedValue),
                "Header '" + headerName + "' value mismatch! Expected to contain: "
                        + expectedValue + " but was: " + header
        );
    }

    @Then("response header {string} should not be null")
    public void validateHeaderNotNull(String headerName) {

        String header = response.getHeader(headerName);

        Assert.assertNotNull(
                header,
                "Header '" + headerName + "' should not be null"
        );
    }

    @Then("response header {string} should be empty")
    public void validateHeaderEmpty(String headerName) {

        String header = response.getHeader(headerName);

        Assert.assertNotNull(
                header,
                "Header '" + headerName + "' is missing"
        );

        Assert.assertTrue(
                header.trim().isEmpty(),
                "Header '" + headerName + "' is not empty. Actual value: " + header
        );
    }

    @Then("final response should not contain {string} response body")
    public void validateNoRedirectFollowed(String value) {

        String body = response.getBody().asString();

        Assert.assertFalse(
                body.contains(value),
                "Redirect was followed unexpectedly. Response body contains: " + value
        );
    }
}