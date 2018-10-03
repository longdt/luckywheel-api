package com.foxpify.luckywheel.model.entity;

import com.foxpify.shopifyapi.model.OAuthToken;
import com.foxpify.shopifyapi.model.User;

import java.time.OffsetDateTime;

public class ShopToken {
    private Long id;
    private String shop;
    private String accessToken;
    private String scope;
    private Long expiresIn;
    private String associatedUserScope;
    private User associatedUser;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean deleted;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
