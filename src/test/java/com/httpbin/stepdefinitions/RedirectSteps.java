package com.httpbin.stepdefinitions;

import com.httpbin.managers.ScenarioContext;
import com.httpbin.utils.ConfigReader;
import com.httpbin.utils.ExcelUtility;
import com.httpbin.utils.RequestBuilder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RedirectSteps {
	private String token;
    private Response response;
    private boolean followRedirects = false;

    private ScenarioContext scenarioContext = new ScenarioContext();

    @Given("base URL is set to {string}")
    public void setBaseUrl(String baseUrl) {
        RequestBuilder.setBaseUri(baseUrl);
    }

    @Given("auto redirect is disabled")
    public void disableAutoRedirect() {
        followRedirects = false;
    }
    
    @When("I send authenticated request using bearer token")
    public void sendAuthenticatedRequestUsingBearerToken() {

       
        RequestSpecification request = RequestBuilder.getRequestBearer();

        response = request.get("/bearer");
        token = response.jsonPath().getString("token");
        System.out.println("Authentication Token :" + token);

        logResponse("GET", "/bearer");
    }
    
    

    @When("user sends {string} request to {string}")
    public void sendRequest(String method, String endpoint) {

        RequestSpecification request =
                RequestBuilder.getRequest(followRedirects).header("Authorization", "Bearer " + token); 

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

        logResponse(method, endpoint);
    }

    @When("user sends GET request with following URLs")
    public void sendMultipleRequests(DataTable table) {

        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {

            String url = row.get("url");

            response = RequestBuilder
                    .getRequest(followRedirects).header("Authorization", "Bearer " + token)
                    .get("/redirect-to?url=" + url);

            logResponse("GET", "/redirect-to?url=" + url);

            Assert.assertEquals(response.getStatusCode(), 302);

            String location = response.getHeader("Location");
            Assert.assertTrue(location.contains(url));
        }
    }

    @When("user sends request to {string} using test data")
    public void sendRequestUsingExcel(String endpoint) throws Exception {

        List<Map<String, String>> dataList = new ExcelUtility().getSheetData("Sheet4");

        for (Map<String, String> data : dataList) {

            String url = data.get("url");
            String method = data.get("method");

            if (url == null || url.trim().isEmpty()) {
                continue;
            }

            RequestSpecification request =
                    RequestBuilder.getRequest(followRedirects).header("Authorization", "Bearer " + token);

            String finalEndpoint = endpoint + "?url=" + url;

            switch (method.toUpperCase()) {

                case "GET":
                    response = request.get(finalEndpoint);
                    break;
                case "POST":
                    response = request.post(finalEndpoint);
                    break;
                case "PUT":
                    response = request.put(finalEndpoint);
                    break;
                case "PATCH":
                    response = request.patch(finalEndpoint);
                    break;
                case "DELETE":
                    response = request.delete(finalEndpoint);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid method: " + method);
            }

            logResponse(method, finalEndpoint);

            Assert.assertEquals(response.getStatusCode(), 302);

            String location = response.getHeader("Location");

            Assert.assertNotNull(location);

            Assert.assertTrue(
                    location.startsWith(url.split("\\?")[0])
            );
        }
    }

    @Then("response status code should be {int}")
    public void validateStatusCode(int expectedStatusCode) {

        Assert.assertNotNull(response);

        Assert.assertEquals(response.getStatusCode(), expectedStatusCode);
    }

    @Then("response header {string} should contain {string}")
    public void validateHeaderContains(String headerName, String expectedValue) {

        String header = response.getHeader(headerName);

        Assert.assertNotNull(header);

        Assert.assertTrue(header.contains(expectedValue));
    }

    @Then("response header {string} should not be null")
    public void validateHeaderNotNull(String headerName) {

        Assert.assertNotNull(response.getHeader(headerName));
    }

    @Then("response header {string} should be empty")
    public void validateHeaderEmpty(String headerName) {

        String header = response.getHeader(headerName);

        Assert.assertNotNull(header);

        Assert.assertTrue(header.trim().isEmpty());
    }

    @Then("final response should not contain {string} response body")
    public void validateNoRedirectFollowed(String value) {

        String body = response.getBody().asString();

        Assert.assertFalse(body.contains(value));
    }

    @Then("response body should contain {string}")
    public void validateResponseBodyContains(String value) {

        String body = response.getBody().asString();

        Assert.assertTrue(body.contains(value));
    }

    @Then("user extracts {string} header as {string}")
    public void extractHeader(String headerName, String key) {

        String value = response.getHeader(headerName);

        Assert.assertNotNull(value);

        scenarioContext.set(key, value);
    }

    @When("user sends {string} request to extracted {string}")
    public void sendRequestUsingExtractedValue(String method, String key) {

        String endpoint = (String) scenarioContext.get(key);

        Assert.assertNotNull(endpoint);

        sendRequest(method, endpoint);
    }

    @Then("response should match JSON schema for redirect {string}")
    public void validateSchema(String schemaFile) {

        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(
                        new File("src/test/resources/schemas/" + schemaFile)
                ));
    }

    private void logResponse(String method, String endpoint) {

        System.out.println("====================================");
        System.out.println("Request Method: " + method);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("====================================");
    }
}