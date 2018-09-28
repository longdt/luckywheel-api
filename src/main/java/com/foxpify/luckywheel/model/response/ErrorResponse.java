package com.foxpify.luckywheel.model.response;

import com.dslplatform.json.CompiledJson;

import java.util.concurrent.ThreadLocalRandom;

public class ErrorResponse {
    private final String id;
    private final String error;
    private final String message;
    private final String description;

    private final int ID_LENGTH = 5;
    private final int charA = (int) 'a';
    private final int charZPlusOne = (int) 'z' + 1;


    public ErrorResponse(String error, String message) {
        this(error, message, null);
    }

    @CompiledJson
    public ErrorResponse(String error, String message, String description) {
        this.id = generateId();
        this.error = error;
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

    public String getError() {
        return error;
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
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
