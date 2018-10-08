package com.foxpify.luckywheel.model.request;

import com.dslplatform.json.CompiledJson;

import java.util.UUID;

@CompiledJson
public class SubscribeRequest {
    private UUID campaignId;
    private String fullName;
    private String email;

    public UUID getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(UUID campaignId) {
        this.campaignId = campaignId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
