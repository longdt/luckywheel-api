package com.foxpify.luckywheel.model.entity;

import com.foxpify.shopifyapi.model.OAuthToken;
import com.foxpify.shopifyapi.model.User;

import java.time.OffsetDateTime;

public class ShopToken {
    private String shop;
    private boolean permanentToken;
    private String code;
    private String accessToken;
    private String scope;
    private Long expiresIn;
    private String associatedUserScope;
    private User associatedUser;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ShopToken() {
    }

    public ShopToken(String shop, String code, boolean permanentToken, OAuthToken authToken) {
        this.shop = shop;
        this.permanentToken = permanentToken;
        this.code = code;
        this.accessToken = authToken.getAccessToken();
        this.scope = authToken.getScope();
        this.expiresIn = authToken.getExpiresIn();
        this.associatedUserScope = authToken.getAssociatedUserScope();
        this.associatedUser = authToken.getAssociatedUser();
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public boolean isPermanentToken() {
        return permanentToken;
    }

    public void setPermanentToken(boolean permanentToken) {
        this.permanentToken = permanentToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAssociatedUserScope() {
        return associatedUserScope;
    }

    public void setAssociatedUserScope(String associatedUserScope) {
        this.associatedUserScope = associatedUserScope;
    }

    public User getAssociatedUser() {
        return associatedUser;
    }

    public void setAssociatedUser(User associatedUser) {
        this.associatedUser = associatedUser;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
