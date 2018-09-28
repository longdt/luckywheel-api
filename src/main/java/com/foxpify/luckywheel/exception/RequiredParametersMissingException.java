package com.foxpify.luckywheel.exception;

public class RequiredParametersMissingException extends BusinessException {
    public RequiredParametersMissingException(String message) {
        super(ErrorCode.REQUIRED_PARAMETERS_MISSING, message);
    }
}
