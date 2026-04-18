package com.httpbin.managers;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private static Map<String, Object> context = new HashMap<>();

    public static void set(String key, Object value) {
        context.put(key, value);
    }

    public static Object get(String key) {
        return context.get(key);
    }
}