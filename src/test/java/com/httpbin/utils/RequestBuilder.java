package com.httpbin.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

    public static RequestSpecification getRequest() {
        return RestAssured.given()
                .header("Content-Type", "application/json");
    }
}