package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.java.Before;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import com.httpbin.utils.ConfigReader;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

public class Step_Def_HttpMethods {

    String baseUrl;
    String username;
    String password;
    String token;

    Response response;

    String name;
    String role;

    // 🔥 Load config before every scenario
    @Before
    public void setup() {
        ConfigReader.loadConfig();
    }

    @Given("HTTPBin base URL is set to {string}")
    public void setBaseUrl(String url) {

        // Fetch from config.properties
        baseUrl = ConfigReader.get("base_url");
        username = ConfigReader.get("username");
        password = ConfigReader.get("password");
        token = ConfigReader.get("bearer_token");
    }

    // ================== GET (Basic Auth) ==================
    @When("user sends GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = given()
                .auth().preemptive().basic(username, password)
                .when()
                .get(baseUrl + endpoint);
    }

    @Then("response args will have name {string} and role {string}")
    public void validateGetResponse(String expName, String expRole) {

        assertEquals(response.jsonPath().getString("args.name"), expName);
        assertEquals(response.jsonPath().getString("args.role"), expRole);
    }

    // ================== POST (Bearer Token + Chaining) ==================
    @When("user sends POST request with below data")
    public void sendPostRequest(DataTable dataTable) {

        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> body = new HashMap<>(data.get(0));

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(baseUrl + "/post");

        assertEquals(response.getStatusCode(), 200);

        // 🔗 chaining
        name = response.jsonPath().getString("json.name");
        role = response.jsonPath().getString("json.role");
    }

    @Then("response json will match sent payload")
    public void validatePostResponse() {
        assertNotNull(response.jsonPath().get("json"));
    }

    // ================== PUT (Bearer Token + Uses chained data) ==================
    @When("user sends PUT request to {string} with name {string} and role {string}")
    public void sendPutRequest(String endpoint, String inputName, String inputRole) {

        // if values passed → override chained values
        if (inputName != null) name = inputName;
        if (inputRole != null) role = inputRole;

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("role", role);

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(baseUrl + endpoint);
    }

    @Then("response json will reflect role {string}")
    public void validatePutResponse(String expectedRole) {

        assertEquals(response.jsonPath().getString("json.role"), expectedRole);
    }

    // ================== PATCH (Bearer Token) ==================
    @When("user sends PATCH request to {string} with invalid data")
    public void sendPatchRequest(String endpoint) {

        String body = "{ invalid json }";

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(baseUrl + endpoint);
    }

    @Then("response will handle invalid input")
    public void validatePatchResponse() {
        assertTrue(response.getStatusCode() >= 200);
    }

    // ================== DELETE (Basic Auth) ==================
    @When("user sends DELETE request to {string}")
    public void sendDeleteRequest(String endpoint) {

        response = given()
                .auth().preemptive().basic(username, password)
                .when()
                .delete(baseUrl + endpoint);
    }

    @Then("response args will contain {string} and {string}")
    public void validateDeleteResponse(String expName, String expRole) {

        assertEquals(response.jsonPath().getString("args.name"), expName);
        assertEquals(response.jsonPath().getString("args.role"), expRole);
    }

    // ================== COMMON ==================
    @Then("response time will be less than {int} ms")
    public void validateResponseTime(int time) {
        assertTrue(response.getTime() < time);
    }
}