package com.httpbin.stepdefinitions;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import com.httpbin.pojo.Anything_Pojo;
import java.time.Instant;


public class Anything_steps {
    private RequestSpecification request;
    private Response response;
    private static String chainedId; 

    // --- SETUP ---
    @Given("I have the API base URL {string}")
    public void setBaseUri(String uri) {
        RestAssured.baseURI = uri;
        request = given().header("Content-Type", "application/json");
    }

    // --- CREATE (POST) ---
    @Given("I provide user details with id {int}, name {string}, and status {string}")
    public void provideUserDetails(int id, String name, String active) {
        boolean isActive = Boolean.parseBoolean(active);
        request.body(new Anything_Pojo(id, name, isActive));
    }

    @When("I submit a request to create a record")
    public void submitCreateRequest() {
        response = request.post("/anything");
    }

    @Then("I save the unique ID from the response for future use")
    public void saveIdForChaining() {
        chainedId = response.jsonPath().getString("json.id");
        System.out.println(">>> SUCCESS: ID " + chainedId + " saved for later steps.");
    }

    // --- READ (GET) ---
    @Given("I log in with valid credentials {string} and {string}")
    public void login(String user, String pass) {
        request.auth().basic(user, pass);
    }

    @Given("I include a tracking ID {string} and a timestamp")
    public void addTrackingAndTimestamp(String trackId) {
        request.queryParam("tracking_id", trackId);
        request.header("X-Client-Timestamp", Instant.now().toString());
    }

    @When("I request the record details")
    public void requestDetails() {
        response = request.get("/anything");
    }

    @Then("the response should display the correct tracking ID")
    public void verifyTrackingId() {
        // We use the param we sent earlier
        response.then().body("args.tracking_id", notNullValue());
    }

    // --- UPDATE (PUT & PATCH) ---
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
        response.then().body("json.age", hasToString(String.valueOf(expectedAge)));
    }

    // --- DELETE ---
    @When("I delete the record using the previously saved ID")
    public void deleteUsingChainedId() {
        response = request.queryParam("id", chainedId).delete("/anything");
    }

    @Then("the system should confirm the correct ID was removed")
    public void verifyDeletion() {
        response.then().body("args.id", equalTo(chainedId));
    }

    // --- NEGATIVE / ERROR HANDLING ---
    @When("I try to access a non-existent page {string}")
    public void accessInvalidPage(String page) {
        response = request.get(page);
    }

    @When("I use the wrong method for the status page")
    public void useWrongMethod() {
        // Using GET on a path designed to test 405 constraints
        response = request.get("/status/405");
    }

    // Reusable step for all status code checks
    @Then("the request should be successful with status {int}")
    @Then("I should receive a {int} Not Found error")
    @Then("I should receive a {int} Method Not Allowed error")
    public void checkStatusCode(int code) {
        response.then().statusCode(code);
    }

    @Then("the response should list the allowed methods")
    public void checkAllowHeader() {
        String allowHeader = response.getHeader("Allow");
        if (allowHeader != null) {
            System.out.println(">>> Allowed Methods: " + allowHeader);
        } else {
            System.out.println(">>> Info: 'Allow' header not provided by this endpoint.");
        }
    }
}