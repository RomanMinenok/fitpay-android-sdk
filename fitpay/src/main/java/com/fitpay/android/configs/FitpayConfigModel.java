package com.fitpay.android.configs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.StringUtils;

/**
 * Fitpay config model. Internal class
 */
class FitpayConfigModel {
    private String clientId;
    private String webUrl;
    private String redirectUrl;
    private String apiURL;
    private String authURL;
    private boolean skipHealthCheck;
    private FitpayConfigWebModel web;

    FitpayConfigModel() {
    }

    FitpayConfigModel(@NonNull String clientId) {
        this.clientId = clientId;
    }

    String getClientId() {
        return !StringUtils.isEmpty(clientId) ? clientId : Constants.CONFIG_CLIENT_ID;
    }

    String getWebUrl() {
        return !StringUtils.isEmpty(webUrl) ? webUrl : Constants.CONFIG_WV_URL;
    }

    String getRedirectUrl() {
        return !StringUtils.isEmpty(redirectUrl) ? redirectUrl : Constants.CONFIG_REDIRECT_URL;
    }

    String getApiURL() {
        return !StringUtils.isEmpty(apiURL) ? apiURL : Constants.CONFIG_API_BASE_URL;
    }

    String getAuthURL() {
        return !StringUtils.isEmpty(authURL) ? authURL : Constants.CONFIG_AUTH_BASE_URL;
    }

    boolean skipHealthCheck() {
        return skipHealthCheck;
    }

    FitpayConfigWebModel getWebConfig() {
        if(web == null){
            web = new FitpayConfigWebModel();
        }
        return web;
    }
}
