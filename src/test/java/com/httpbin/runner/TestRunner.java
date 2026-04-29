package com.httpbin.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(

    features = "src/test/resources/features/Status_code.feature ",

    glue = {"com.httpbin.stepdefinitions", "com.httpbin.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-report.html",
        "json:target/cucumber.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    publish = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}