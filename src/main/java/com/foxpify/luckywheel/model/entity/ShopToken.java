package com.foxpify.luckywheel.model.entity;

import com.foxpify.shopifyapi.model.OAuthToken;
import com.foxpify.shopifyapi.model.User;

import java.time.OffsetDateTime;

public class ShopToken {
    private String shop;
    private String accessToken;
    private String scope;
    private long expiresIn;
    private String associatedUserScope;
    private User associatedUser;
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;

    public ShopToken() {
    }

    public ShopToken(String shop, OAuthToken authToken) {
        this.shop = shop;
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

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
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

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
