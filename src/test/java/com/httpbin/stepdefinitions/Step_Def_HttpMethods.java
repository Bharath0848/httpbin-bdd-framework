package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

public class Step_Def_HttpMethods {

    String baseUrl;
    Response response;

  
    @Given("I set base URL")
    public void setBaseUrl() {
        baseUrl = "https://httpbin.org";
    }

    @When("I send GET request with valid query params")
    public void sendGetRequest() {

        response = given()
                .queryParam("name", "arthi")
                .queryParam("role", "tester")
                .when()
                .get(baseUrl + "/get");
    }

    @Then("I validate GET response")
    public void validateGet() {
        assertEquals(response.getStatusCode(), 200);
    }


    @When("I send POST request with valid JSON")
    public void sendPost() {

        String body = "{ \"project\": \"API Testing\", \"status\": \"running\" }";

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(baseUrl + "/post");
    }

    @Then("I validate POST response")
    public void validatePost() {
        assertEquals(response.getStatusCode(), 200);
    }

  
    @When("I send PUT request with valid data")
    public void sendPut() {

        String body = "{ \"task\": \"update profile\", \"status\": \"completed\" }";

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(baseUrl + "/put");
    }

    @Then("I validate PUT response")
    public void validatePut() {
        assertEquals(response.getStatusCode(), 200);
    }

    @When("I send PATCH request with invalid data")
    public void sendPatch() {

        String body = "{ \"invalid\": \"data\" }";

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(baseUrl + "/patch");
    }

    @Then("I validate PATCH response")
    public void validatePatch() {
        assertEquals(response.getStatusCode(), 200);
    }

    @When("I send DELETE request with valid params")
    public void sendDelete() {

        response = given()
                .queryParam("id", "101")
                .when()
                .delete(baseUrl + "/delete");
    }

    @Then("I validate DELETE response")
    public void validateDelete() {
        assertEquals(response.getStatusCode(), 200);
    }
}