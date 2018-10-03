package com.foxpify.luckywheel.model.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Wheel {
    private UUID id;
    private String shop;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String name;
    private Boolean active;
    private Float winProbability;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private List<Slide> slides;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public Float getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(Float winProbability) {
        this.winProbability = winProbability;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }
}
