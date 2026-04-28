package com.httpbin.stepdefinitions;

import io.cucumber.java.en.Given;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import io.cucumber.datatable.DataTable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.httpbin.utils.ConfigReader;
import com.httpbin.utils.ExcelUtility;

public class Status_code {

    Response response;
    int chainedStatusCode;
    int secondCode;
    ExcelUtility eUtil = new ExcelUtility();

    @Given("the base url of httpbin")
    public void set_base_url() {
        ConfigReader.loadConfig();
        baseURI = ConfigReader.get("base_url") + "/status";
    }

    @When("the post request is sent with status code {int}")
    public void sent_post_request(int code) {
        response = given()
                .auth().oauth2(ConfigReader.get("bearer_token"))
                .contentType(ContentType.JSON)
                .body("{\"post\":\"applied\"}")
                .pathParam("code", code)
        .when()
                .post("/{code}");
    }

    @When("the get request is sent with status code {int}")
    public void sent_get_request(int code) {
        response = given()
               .auth().basic(
                        ConfigReader.get("username"),
                        ConfigReader.get("password"))
                .pathParam("code", code)
        .when()
                .get("/{code}");

        chainedStatusCode = response.getStatusCode();
    }

    @When("the put request is sent with chained status code and below data")
    public void sent_put_request_with_table(DataTable table) {

        List<Map<String, String>> data = table.asMaps(String.class, String.class);

        secondCode = Integer.parseInt(data.get(0).get("secondCode"));

        String codes = chainedStatusCode + "," + secondCode;

        response = given()
                .auth().oauth2(ConfigReader.get("bearer_token"))
                .contentType(ContentType.JSON)
                .body("{\"put\":\"applied\"}")
                .pathParam("codes", codes)
        .when()
                .put("/{codes}");
    }

    @When("the patch request is sent without status code")
    public void sent_patch_request() {
        response = given()
                .auth().oauth2(ConfigReader.get("bearer_token"))
                .contentType(ContentType.JSON)
                .body("{\"patch\":\"applied\"}")
        .when()
                .patch("/");
    }

    @When("the delete request is sent using excel data {string}")
    public void sent_delete_request_excel(String sheetName) throws IOException {

        List<Map<String, String>> data = eUtil.getSheetData(sheetName);
        int code = Integer.parseInt(data.get(0).values().iterator().next());
        

        response = given()
                .auth().basic(
                        ConfigReader.get("username"),
                        ConfigReader.get("password"))
                .pathParam("code", code)
        .when()
                .delete("/{code}");
    }

    @Then("we get the response code as {int}")
    public void validate_status_code(int code) {
        response.then().statusCode(code);
    }

    @Then("we get the response code as either chained status code or second code")
    public void validate_multiple_status_code() {
        response.then().statusCode(anyOf(is(chainedStatusCode), is(secondCode)));
    }

    @Then("the response time is less than {int} ms")
    public void validate_response_time(int time) {
        response.then().time(lessThan((long) time));
    }
}