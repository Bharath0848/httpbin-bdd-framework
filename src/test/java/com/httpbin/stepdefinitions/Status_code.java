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

public class Status_code {
	Response response;
	@Given("the base url of httpbin")
	public void set_base_url() {
	       baseURI="https://httpbin.org/status";
	}
	@When("the post request is sent with status code {int}")
	public void sent_post_request(int code) {
	    response = given()
	            .auth().oauth2("mytoken")
	            .contentType(ContentType.JSON)
	            .body("{\r\n"
	            		+ "    \"post\":\"applied\"\r\n"
	            		+ "}")
	            .pathParam("code", code)
	    .when()
	            .post("/{code}");
	}
	@When("the get request is sent with status code {int}")
	public void sent_get_request(int code) {
	    response = given()
	            .auth().basic("admin", "admin@123")
	            .pathParam("code", code)
	    .when()
	            .get("/{code}");
	}
	@When("the put request is sent with status codes {string}")
	public void sent_put_request(String codes) {
	    response = given()
	            .auth().oauth2("mytoken")
	            .contentType(ContentType.JSON)
	            .body("{\r\n"
	            		+ "    \"put\":\"applied\"\r\n"
	            		+ "}")
	            .pathParam("codes", codes)
	    .when()
	            .put("/{codes}");
	}
	@When("the patch request is sent without status code")
	public void sent_patch_request() {
	    response = given()
	            .auth().oauth2("mytoken")
	            .contentType(ContentType.JSON)
	            .body("{\r\n"
	            		+ "    \"patch\":\"applied\"\r\n"
	            		+ "}")
	    .when()
	            .patch("/");
	}
	@When("the delete request is sent with status code {int}")
	public void sent_delete_request(int code) {
	    response = given()
	            .auth().basic("admin", "admin@123")
	            .pathParam("code", code)
	    .when()
	            .delete("/{code}");
	}
	@Then("we get the response code as {int}")
	public void validate_status_code(int code) {
		response.then().statusCode(code);
	}
	@Then("we get the response code as either 200 or 500")
	public void validate_multiple_status_code() {
	    response.then().statusCode(anyOf(is(200), is(500)));
	}
	@Then("the response time is less than {int} ms")
	public void validate_reponse_time(int time) {
		response.then().time(lessThan((long) time));
	}

}
