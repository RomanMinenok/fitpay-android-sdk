package com.fitpay.android.a2averification;

import android.util.Base64;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

/**
 * Issuer response data for {@link A2AVerificationRequest}
 */
public class A2AIssuerResponse {
    @A2AStepupResult.Response
    private String response;
    private String authCode;

    public String getResponse() {
        return response;
    }

    public String getAuthCode() {
        return authCode;
    }

    public A2AIssuerResponse(@A2AStepupResult.Response String response, String authCode) {
        this.response = response;
        this.authCode = authCode;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getEncodedString() {
        byte[] bytesToEncode = toString().getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(bytesToEncode, Base64.URL_SAFE);
    }
}
