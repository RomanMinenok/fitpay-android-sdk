package com.fitpay.android;

import com.fitpay.android.api.ApiManager;

import java.util.HashMap;
import java.util.Map;

/**
 */
public final class TestConstants {
    
    static Map<String, String> getConfig() {
        Map<String, String> env = System.getenv();

        String currentEnv = System.getProperty("environment", "demo");

        Map<String, String>  config = new HashMap<>();
        switch (currentEnv) {
            case "qa":
                config.put(ApiManager.PROPERTY_API_BASE_URL, "https://api.qa.fitpay.ninja/");
                config.put(ApiManager.PROPERTY_AUTH_BASE_URL, "https://auth.qa.fitpay.ninja");
                config.put(ApiManager.PROPERTY_CLIENT_ID, "fitpay");
                config.put(ApiManager.PROPERTY_REDIRECT_URI, "https://api.qa.fitpay.ninja");
                break;

            case "demo":
                config.put(ApiManager.PROPERTY_API_BASE_URL, "https://gi-de.pagare.me/api/");
                config.put(ApiManager.PROPERTY_AUTH_BASE_URL, "https://gi-de.pagare.me");
                config.put(ApiManager.PROPERTY_CLIENT_ID, "pagare");
                config.put(ApiManager.PROPERTY_REDIRECT_URI, "https://demo.pagare.me");
                break;

            case "dev":
            default:
                config.put(ApiManager.PROPERTY_API_BASE_URL, "http://localhost:9092/");
                config.put(ApiManager.PROPERTY_AUTH_BASE_URL, "http://localhost:9091/");
                config.put(ApiManager.PROPERTY_CLIENT_ID, "fitpay");
                config.put(ApiManager.PROPERTY_REDIRECT_URI, "http://localhost:9092");

        }

        return config;

    }

}
