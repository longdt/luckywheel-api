package com.foxpify.luckywheel.exception;

public class SlideNotFoundException extends BusinessException {

    public SlideNotFoundException(String message) {
        super(ErrorCode.SLICE_NOT_FOUND, message);
    }
}
