package com.foxpify.luckywheel.model.request;

import com.dslplatform.json.CompiledJson;

import java.util.UUID;

@CompiledJson
public class SpinRequest {
    private UUID wheelId;
    private String fullName;
    private String email;

    public UUID getWheelId() {
        return wheelId;
    }

    public void setWheelId(UUID wheelId) {
        this.wheelId = wheelId;
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
