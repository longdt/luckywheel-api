package com.foxpify.luckywheel.exception;

public class InvalidHmacException extends BusinessException {
    public InvalidHmacException(String message) {
        super(ErrorCode.INVALID_HMAC_ERROR, message);
    }
}
