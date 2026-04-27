package com.httpbin.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

    private static RequestSpecification request;

    public static void setBaseUri(String baseUri) {
        RestAssured.baseURI = baseUri;
    }

    public static RequestSpecification getRequest(boolean followRedirects) {
    	 String token = ConfigReader.get("bearer_token");
    	 
        request = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .redirects()
                .follow(followRedirects); 

        return request;
    }
    public static RequestSpecification getRequestBearer() {
   	 String token = ConfigReader.get("bearer_token");
   	 
       request = RestAssured
               .given().header("Authorization", "Bearer " + token); 

       return request;
   }
}