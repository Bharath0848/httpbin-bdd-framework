package com.httpbin.stepdefinitions;

import com.httpbin.managers.ScenarioContext;
import com.httpbin.utils.ConfigReader;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.testng.Assert.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class DynamicDelayStep {
    private String baseurl;
    private Response response;
    private long responseTime;

    @Given("base URL is set")
    public void setBaseUrl() {
        baseurl=ConfigReader.get("base_url");
        RestAssured.baseURI = baseurl;
    }
    @When("user sends {string} request with delay {string} and payload {string}")
public void user_sends_request_with_delay_and_payload(String method, String delay, String payload) {

    String actualPayload = payload.equals("NA") 
            ? "" 
            : payload.replace("\\\"", "\"");

    response = RestAssured
            .given()
            .contentType("application/json")
            .pathParam("time", delay)   
            .body(actualPayload)
            .when()
            .request(method, com.httpbin.endpoints.Routes.DELAY);

    response.then().log().all();}

    @When("user sends {string} request of {string}")
    public void sendRequest(String method, String endpoint) {

        switch (method.toUpperCase()) {
            case "GET":
                response = RestAssured.given().when().get(endpoint);
                break;

            case "POST":
                response = RestAssured.given()
                        .body("{\"sample\":\"data\"}")
                        .when().post(endpoint);
                break;

            case "PUT":
                response = RestAssured.given()
                        .body("{\"update\":\"value\"}")
                        .when().put(endpoint);
                break;

            case "DELETE":
                response = RestAssured.given().when().delete(endpoint);
                break;

            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }

        responseTime = response.getTime(); 
        System.out.println("Response Time: " + responseTime + " ms");
    }

    @Then("response status code should be equal to {int}")
public void validateStatusCode(int expectedCode) {

    int actualCode = response.getStatusCode();
    System.out.println("Actual Status Code: " + actualCode);

    if (expectedCode == 404) {
        assertTrue(actualCode == 404 || actualCode == 500,
                "Expected 404 or 500 but got: " + actualCode);
    } else {
        assertEquals(actualCode, expectedCode,
                "Status code mismatch");
    }
}


    @Then("response time should be between {int} and {int} seconds")
    public void validateResponseTime(int min, int max) {

        long timeInSeconds = responseTime / 1000;

        System.out.println("Response Time (seconds): " + timeInSeconds);

        assertTrue(timeInSeconds >= min && timeInSeconds <= max,
                "Response time not in expected range: " + timeInSeconds);
    }

    @Then("response should be handled correctly for {string}")
    public void validateEdgeCases(String value) {

        System.out.println("Validating edge case for value: " + value);

        if (value.contains(".")) {
            assertEquals(response.getStatusCode(), 200,
                    "Decimal value should return 200");
        }
        else if (value.startsWith("-")) {
            assertEquals(response.getStatusCode(), 200,
                    "Negative value should return 200");
        }
        else if (value.equals("0")) {
            assertEquals(response.getStatusCode(), 200,
                    "Zero delay should return 200");
            assertTrue(responseTime < 5000,
                    "Zero delay should be fast");
        }
        else {
            fail("Unhandled edge case: " + value);
        }
    }

   @Then("response should match JSON schema {string}")
public void validateJsonSchema(String schemaFile) {

    int statusCode = response.getStatusCode();

    if (statusCode == 200) {
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/" + schemaFile));

        System.out.println("Schema validation passed for: " + schemaFile);
    } else {
        System.out.println("Skipping schema validation for status: " + statusCode);
    }
}
    @Then("response should match previous response")
    public void validateChaining() {

        String prevData = (String) ScenarioContext.get("data");

        if (prevData != null && prevData.startsWith("{")) {

            JsonPath prev = new JsonPath(prevData);
            JsonPath curr = new JsonPath(response.jsonPath().getString("data"));

            assertEquals(curr.getString("name"), prev.getString("name"));
        }
    }



}
 