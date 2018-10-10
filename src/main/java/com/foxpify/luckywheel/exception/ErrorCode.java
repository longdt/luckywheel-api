package com.foxpify.luckywheel.exception;

public interface ErrorCode {
    BusinessErrorCode INTERNAL_SERVER_ERROR =
            new BusinessErrorCode("LW-5001", "Internal server error", 500);

    BusinessErrorCode INVALID_HMAC_ERROR =
            new BusinessErrorCode("LW-4001", "Invalid hmac error", 400);

    BusinessErrorCode REQUIRED_PARAMETERS_MISSING_OR_INVALID =
            new BusinessErrorCode("LW-4002", "Required parameters missing or invalid", 400);

    BusinessErrorCode SHOP_NOT_FOUND =
            new BusinessErrorCode("LW-4003", "Shop is not found", 400);

    BusinessErrorCode ORIGIN_CANT_BE_VERIFIED =
            new BusinessErrorCode("LW-4004", "Request origin cannot be verified", 403);

    BusinessErrorCode UNAUTHENTICATED_ERROR =
            new BusinessErrorCode("LW-4005", "Unauthorized error", 401);

    BusinessErrorCode CAMPAIGN_NOT_FOUND =
            new BusinessErrorCode("LW-4006", "Campaign is not found", 400);

    BusinessErrorCode SLICE_NOT_FOUND =
            new BusinessErrorCode("LW-4007", "Slice is not found", 400);

    BusinessErrorCode SUBSCRIBER_EXISTS_ERROR =
            new BusinessErrorCode("LW-4008", "subscriber is already exists", 400);
}
