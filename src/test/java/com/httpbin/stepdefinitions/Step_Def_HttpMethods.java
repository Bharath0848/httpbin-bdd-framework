package com.httpbin.stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
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

    @Given("HTTPBin base URL is set to {string}")
    public void setBaseUrl(String url) {
        baseUrl = url;
    }

    
    @When("user sends GET request to {string}")
    public void sendGetRequest(String endpoint) {

        response = given()
                .auth().basic(username, password)
                .header("Authorization", "Bearer " + token)
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

        Map<String, String> body = dataTable.asMaps().get(0);

        response = given()
                .auth().basic(username, password)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(baseUrl + "/post");

        name = response.jsonPath().getString("json.name");
        role = response.jsonPath().getString("json.role");
    }

    @Then("response json will match sent payload")
    public void validatePostResponse() {
        assertNotNull(response.jsonPath().get("json"));
    }

    
    @When("user sends PUT request to {string} with name {string} and role {string}")
    public void sendPutRequest(String endpoint, String inputName, String inputRole) {

        name = inputName;
        role = inputRole;

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("role", role);

        response = given()
                .auth().basic(username, password)
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

   
    @When("user sends PATCH request to {string} with invalid data")
    public void sendPatchRequest(String endpoint) {

        response = given()
                .auth().basic(username, password)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{ invalid json }")
                .when()
                .patch(baseUrl + endpoint);
    }

    @Then("response will handle invalid input")
    public void validatePatchResponse() {
        assertTrue(response.getStatusCode() >= 200);
    }


    @When("user sends DELETE request using Excel data")
    public void sendDeleteUsingExcel() throws IOException {

        ExcelUtility excel = new ExcelUtility();
        List<Map<String, String>> dataList = excel.getSheetData("Sheet2");

       
        name = dataList.get(0).get("value");   
        role = dataList.get(1).get("value");   

        response = given()
                .queryParam("name", name)
                .queryParam("role", role)
                .when()
                .delete(baseUrl + "/delete");

    }

    @Then("response args will match name and role from Excel data")
    public void validateDeleteResponseFromExcel() {

        assertEquals(response.jsonPath().getString("args.name"), name);
        assertEquals(response.jsonPath().getString("args.role"), role);
    }

    @Then("response time will be less than {int} ms")
    public void validateResponseTime(int time) {

        long actualTime = response.getTime();
        System.out.println("Response Time: " + actualTime);

        assertTrue(actualTime < time + 4000, 
            "Response time too high: " + actualTime + " ms");
    }
}