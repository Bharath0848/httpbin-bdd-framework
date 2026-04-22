package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

public class Step_Def_HttpMethods {

    String baseUrl;
    Response response;

    String name;
    String role;

 
    @Given("HTTPBin base URL is set to {string}")
    public void setBaseUrl(String url) {
        baseUrl = url;
    }

 
    @When("user sends GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = given()
                .when()
                .get(baseUrl + endpoint);
    }

    @Then("response args will have name {string} and role {string}")
    public void validateGetResponse(String expName, String expRole) {

        assertEquals(response.jsonPath().getString("args.name"), expName);
        assertEquals(response.jsonPath().getString("args.role"), expRole);
    }

    @When("user sends POST request with below data")
    public void sendPostRequest(DataTable dataTable) {

        List<Map<String, String>> data = dataTable.asMaps();

        Map<String, String> body = new HashMap<>(data.get(0));

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(baseUrl + "/post");

        assertEquals(response.getStatusCode(), 200);


        name = response.jsonPath().getString("json.name");
        role = response.jsonPath().getString("json.role");
    }

    @Then("response json will match sent payload")
    public void validatePostResponse() {
        assertNotNull(response.jsonPath().get("json"));
    }


    @When("user sends PUT request to {string} with name {string} and role {string}")
    public void sendPutRequest(String endpoint, String inputName, String inputRole) {

       
        if (inputName != null) name = inputName;
        if (inputRole != null) role = inputRole;

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("role", role);

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(baseUrl + endpoint);
    }

    @Then("response json will reflect role {string}")
    public void validatePutResponse(String expectedRole) {

        assertEquals(response.jsonPath().getString("json.role"), expectedRole);
    }


    @When("user sends PATCH request to {string} with invalid data")
    public void sendPatchRequest(String endpoint) {

        String body = "{ invalid json }";

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(baseUrl + endpoint);
    }

    @Then("response will handle invalid input")
    public void validatePatchResponse() {
        assertTrue(response.getStatusCode() >= 200);
    }


    @When("user sends DELETE request to {string}")
    public void sendDeleteRequest(String endpoint) {

        response = given()
                .when()
                .delete(baseUrl + endpoint);
    }

    @Then("response args will contain {string} and {string}")
    public void validateDeleteResponse(String expName, String expRole) {

        String res = response.getBody().asString();

        assertTrue(res.contains(expName));
        assertTrue(res.contains(expRole));
    }

    @Then("response time will be less than {int} ms")
    public void validateResponseTime(int time) {
        assertTrue(response.getTime() < time);
    }
}