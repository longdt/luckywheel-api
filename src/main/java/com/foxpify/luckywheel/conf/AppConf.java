package com.foxpify.luckywheel.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

public class AppConf {
    private String contextPath;
    private int httpPort;
    private String httpHost;
    private String appKey;
    private String appSecret;
    private String adminUrl;
    @JsonProperty("wheel.js.url")
    private String wheelJsUrl;
    @JsonProperty("jwt.token.expiresInSeconds")
    private int jwtExpiresInSeconds;
    private JsonObject jsonObject;

    public AppConf() {
    }

    public AppConf(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public JsonObject getDatasource() {
        return jsonObject.getJsonObject("datasource");
    }

    public JsonObject getJWTAuthOptions() {
        return jsonObject.getJsonObject("jwt.auth.options");
    }

    AppConf setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public int getJwtExpiresInSeconds() {
        return jwtExpiresInSeconds;
    }

    public void setJwtExpiresInSeconds(int jwtExpiresInSeconds) {
        this.jwtExpiresInSeconds = jwtExpiresInSeconds;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getWheelJsUrl() {
        return wheelJsUrl;
    }

    public void setWheelJsUrl(String wheelJsUrl) {
        this.wheelJsUrl = wheelJsUrl;
    }
}
