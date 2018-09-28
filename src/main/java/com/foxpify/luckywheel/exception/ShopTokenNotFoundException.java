package com.foxpify.luckywheel.exception;

public class ShopTokenNotFoundException extends BusinessException {
    public ShopTokenNotFoundException(String message) {
        super(ErrorCode.SHOP_TOKEN_NOT_FOUND, message);
    }
}
