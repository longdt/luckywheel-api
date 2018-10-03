package com.foxpify.luckywheel.exception;

public interface ErrorCode {
    BusinessErrorCode INTERNAL_SERVER_ERROR =
            new BusinessErrorCode("LW-5001", "Internal server error", 500);

    BusinessErrorCode INVALID_HMAC_ERROR =
            new BusinessErrorCode("LW-4001", "Invalid hmac error", 400);

    BusinessErrorCode REQUIRED_PARAMETERS_MISSING =
            new BusinessErrorCode("LW-4002", "Required parameters missing", 400);

    BusinessErrorCode SHOP_TOKEN_NOT_FOUND =
            new BusinessErrorCode("LW-4003", "Shop access token is not found", 400);

    BusinessErrorCode ORIGIN_CANT_BE_VERIFIED =
            new BusinessErrorCode("LW-4004", "Request origin cannot be verified", 403);
}
