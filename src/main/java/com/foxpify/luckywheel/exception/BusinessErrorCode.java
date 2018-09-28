package com.foxpify.luckywheel.exception;

public class BusinessErrorCode {
    private String code;
    private String description;
    private int httpStatus;

    public BusinessErrorCode(String code, String description, int httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
