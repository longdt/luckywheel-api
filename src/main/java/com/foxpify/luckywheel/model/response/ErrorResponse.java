package com.foxpify.luckywheel.model.response;

import com.dslplatform.json.CompiledJson;

import java.util.concurrent.ThreadLocalRandom;

public class ErrorResponse {
    private final String id;
    private final String code;
    private final String message;
    private final String description;

    private final int ID_LENGTH = 5;
    private final int charA = (int) 'a';
    private final int charZPlusOne = (int) 'z' + 1;

    @CompiledJson
    ErrorResponse(String id, String code, String message, String description) {
        this.id = id;
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public ErrorResponse(String code, String message, String description) {
        this.id = generateId();
        this.code = code;
        this.message = message;
        this.description = description;
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< ID_LENGTH; i++) {
            int ascii = ThreadLocalRandom.current().nextInt(charA, charZPlusOne);
            sb.append((char) ascii);
        }
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
