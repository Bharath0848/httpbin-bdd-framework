package com.httpbin.hooks;

import io.cucumber.java.Before;
import com.httpbin.base.BaseTest;

public class Hooks {

    @Before
    public void beforeScenario() {
        BaseTest.setup();
    }
}