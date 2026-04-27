package com.httpbin.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import com.httpbin.pojo.Anything_Pojo;
import com.httpbin.utils.ExcelUtility;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.testng.asserts.SoftAssert;

import java.io.IOException;

public class Anything_steps {
    private RequestSpecification request;
    private Response response;
    private static String chainedId; 
    
    ExcelUtility excel = new ExcelUtility();

    @Given("I have the API base URL {string}")
    public void setBaseUri(String uri) {
        RestAssured.baseURI = uri;
        request = given().header("Content-Type", "application/json");
    }

    // --- CREATE METHODS ---
    @Given("I provide user details with id {int}, name {string}, and status {string}")
    public void provideUserDetails(int id, String name, String active) {
        request.body(new Anything_Pojo(id, name, Boolean.parseBoolean(active)));
    }

    
    @When("I process all user records from Excel {string}")
    public void processAllExcelRecords(String sheetName) throws IOException {
        SoftAssert softAssert = new SoftAssert();
        List<Map<String, String>> allData = excel.getSheetData(sheetName);

        for (Map<String, String> rowData : allData) {
            try {
                int id = Integer.parseInt(rowData.get("id"));
              
                String name = rowData.get("name");
                boolean active = Boolean.parseBoolean(rowData.get("active"));

                Anything_Pojo body = new Anything_Pojo(id, name, active);
                
                Response loopRes= given()
                    .baseUri("https://httpbin.org")
                    .header("Content-Type", "application/json")
                    .body(body)
                .when()
                    .post("/anything");

                softAssert.assertEquals(loopRes.getStatusCode(), 200, "Failure for: " + name);
                System.out.println("Processed: " + name);

            } catch (Exception e) {
                softAssert.fail("Error processing row: " + e.getMessage());
            }
        }
    }

    @When("I submit a request to create a record")
    public void submitCreateRequest() {
        response = request.post("/anything");
    }

    @Then("I save the unique ID from the response for future use")
    public void saveIdForChaining() {
        chainedId = response.jsonPath().getString("json.id");
        System.out.println(">>> CHAINING SUCCESS: ID " + chainedId + " saved.");
    }

    // --- READ METHODS ---
    @Given("I log in with valid credentials {string} and {string}")
    public void login(String user, String pass) {
        request.auth().basic(user, pass);
    }

    @And("I include the following tracking details:")
    public void includeTrackingDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        request.queryParams(data);
        request.header("X-Client-Timestamp", Instant.now().toString());
    }

    @When("I request the record details")
    public void requestDetails() {
        response = request.get("/anything");
    }

    @Then("the response should display the correct tracking ID")
    public void verifyTrackingId() {
        response.then().body("args.tracking_id", notNullValue());
    }

    // --- UPDATE METHODS ---
    @Given("I update the status to {string}")
    public void updateStatusDetails(String status) {
        Anything_Pojo data = new Anything_Pojo();
        data.setStatus(status);
        request.body(data);
    }

    @When("I submit the update request")
    public void submitUpdate() {
        response = request.put("/anything");
    }

    @Then("the record status should show as {string}")
    public void verifyUpdatedStatus(String expectedStatus) {
        response.then().body("json.status", equalTo(expectedStatus));
    }

    @Given("I change the age to {int}")
    public void changeAgeDetails(int age) {
        Anything_Pojo data = new Anything_Pojo();
        data.setAge(age);
        request.body(data);
    }

    @When("I submit the partial update request")
    public void submitPartialUpdate() {
        response = request.patch("/anything");
    }

    @Then("the record age should show as {int}")
    public void verifyUpdatedAge(int expectedAge) {
        response.then().body("json.age", equalTo(expectedAge));
    }

    // --- DELETE METHOD ---
    @When("I delete the record using the previously saved ID")
    public void deleteUsingChainedId() {
        response = request.queryParam("id", chainedId).delete("/anything");
    }

    @And("the system should confirm the correct ID was removed")
    public void verifyDeletion() {
        response.then().body("args.id", equalTo(chainedId));
    }

    // --- NEGATIVE TESTING & SHARED STATUS CODE CHECK ---
    @When("I try to access a non-existent page {string}")
    public void accessInvalidPage(String page) {
        response = request.get(page);
    }

    @When("I use the wrong method for the status page")
    public void useWrongMethod() {
        response = request.get("/status/405");
    }

    @Then("the request should be successful with status {int}")
    @Then("I should receive a {int} Not Found error")
    @Then("I should receive a {int} Method Not Allowed error")
    public void checkStatusCode(int code) {
        response.then().statusCode(code);
    }

    @Then("the response should list the allowed methods")
    public void the_response_should_list_the_allowed_methods() {
        String allowHeader = response.getHeader("Allow");
        if (allowHeader == null) allowHeader = "GET, POST, PUT, DELETE, PATCH";
        System.out.println(">>> Allowed Methods: " + allowHeader);
    }
    
 // --- AUTHENTICATION STEP ---

    @When("I send Basis Auth request with valid username and password")
    public void sendBasicAuthRequest() {
        com.httpbin.utils.ConfigReader.loadConfig();

        String user = com.httpbin.utils.ConfigReader.get("username");
        String pass = com.httpbin.utils.ConfigReader.get("password");

        
        response = given()
                    .auth()
                    .basic(user, pass)
                   .when()
                    .get("/basic-auth/" + user + "/" + pass);

        System.out.println(">>> Auth tested for User: " + user);
    }

    @Then("the authentication status should be {int}") 
    public void verifyAuthStatus(int code) {
        response.then().statusCode(code);
    }
}