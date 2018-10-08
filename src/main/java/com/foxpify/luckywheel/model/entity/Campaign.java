package com.foxpify.luckywheel.model.entity;

import com.dslplatform.json.CompiledJson;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@CompiledJson
public class Campaign {
    private UUID id;
    private Long shopId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String name;
    private String description;
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

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
