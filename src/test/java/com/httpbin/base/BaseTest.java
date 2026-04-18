package com.httpbin.base;

import io.restassured.RestAssured;
import com.httpbin.utils.ConfigReader;

public class BaseTest {

    public static void setup() {
        ConfigReader.loadConfig();
        RestAssured.baseURI = ConfigReader.get("base_url");
    }
}