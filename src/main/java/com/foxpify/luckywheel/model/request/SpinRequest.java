package com.foxpify.luckywheel.model.request;

import com.dslplatform.json.CompiledJson;

@CompiledJson
public class SpinRequest {
    private Long wheelId;
    private String fullname;
    private String email;

    public Long getWheelId() {
        return wheelId;
    }

    public void setWheelId(Long wheelId) {
        this.wheelId = wheelId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
