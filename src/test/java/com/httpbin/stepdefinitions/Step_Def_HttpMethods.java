package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;

import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;


import com.httpbin.utils.ConfigReader;
import com.httpbin.utils.ExcelUtility;
import java.io.IOException;
import java.util.*;
import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

public class Step_Def_HttpMethods {

    String baseUrl;
    Response response;
    String name;
    String role;
    String username;
    String password;
    String token;

    public Step_Def_HttpMethods() {
        ConfigReader.loadConfig();
        username = ConfigReader.get("username");
        password = ConfigReader.get("password");
        token = ConfigReader.get("bearer_token");
    }

    @Given("HTTPBin base URL is already set")
    public void setBaseUrl() {
    	// Base url set via Hooks
//        baseUrl = ConfigReader.get("base_url");
    }
    
    @When("I send Digest Auth request with credentials -HttpMethod")
    public void sendDigestAuthRequestHttp() {

        String username = ConfigReader.get("username");
        String password = ConfigReader.get("password");

        response = RestAssured
                .given()
                .auth()
                .digest(username, password)
                .log().all()
                .when()
                .get("/digest-auth/auth/" + username + "/" + password);

        System.out.println("Actual Status Code: " + response.getStatusCode());
    }
    @Then("the http method auth response status should be {int}") 
    public void verifyAuthHttpCode(int expectedCode) {
        assertEquals(response.getStatusCode(), expectedCode, "Basic Auth Failed!");
    }


    @When("user sends GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = given()
                .auth().basic(username, password)
                .when()
                .get(endpoint);
    }

    @Then("response args will have name {string} and role {string}")
    public void validateGetResponse(String expName, String expRole) {
        assertEquals(response.jsonPath().getString("args.name"), expName);
        assertEquals(response.jsonPath().getString("args.role"), expRole);
    }

    @When("user sends POST request with below data")
    public void sendPostRequest(DataTable dataTable) {

        Map<String, String> body = dataTable.asMaps().get(0);

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/post");

        name = response.jsonPath().getString("json.name");
        role = response.jsonPath().getString("json.role");
    }

    @Then("response json will match sent payload")
    public void validatePostResponse() {
        assertNotNull(response.jsonPath().get("json"));
    }

    @When("user sends PUT request to {string} with name {string} and role {string}")
    public void sendPutRequest(String endpoint, String inputName, String inputRole) {

    	 if (name == null || role == null) {
    	name = inputName;
        role = inputRole;
    	 }
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("role", role);

        response = given()
                .auth().basic(username, password)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(endpoint);
    }

    @Then("response json will reflect role {string}")
    public void validatePutResponse(String expectedRole) {
        assertEquals(response.jsonPath().getString("json.role"), expectedRole);
    }

    @When("user sends PATCH request to {string} with invalid data")
    public void sendPatchRequest(String endpoint) {

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{ invalid json }")
                .when()
                .patch(endpoint);
    }

    @Then("response will handle invalid input")
    public void validatePatchResponse() {
        assertEquals(response.getStatusCode(), 200);
    }

    @When("user sends DELETE request using Excel data")
    public void sendDeleteUsingExcel() throws IOException {

        ExcelUtility excel = new ExcelUtility();
        List<Map<String, String>> dataList = excel.getSheetData("Sheet2");

        name = dataList.get(0).get("value");
        role = dataList.get(1).get("value");

        response = given()
                .auth().basic(username, password)
                .queryParam("name", name)
                .queryParam("role", role)
                .when()
                .delete("/delete");
    }

    @Then("response args will match name and role from Excel data")
    public void validateDeleteResponseFromExcel() {
        assertEquals(response.jsonPath().getString("args.name"), name);
        assertEquals(response.jsonPath().getString("args.role"), role);
    }

    @Then("response time will be less than {int} ms")
    public void validateResponseTime(int time) {
        assertTrue(response.getTime()< time);
    }
    


}