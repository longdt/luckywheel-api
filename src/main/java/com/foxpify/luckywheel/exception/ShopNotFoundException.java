package com.foxpify.luckywheel.exception;

public class ShopNotFoundException extends BusinessException {
    public ShopNotFoundException(String message) {
        super(ErrorCode.SHOP_NOT_FOUND, message);
    }
}
