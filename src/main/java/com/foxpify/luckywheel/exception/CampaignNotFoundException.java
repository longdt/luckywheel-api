package com.foxpify.luckywheel.exception;

public class CampaignNotFoundException extends BusinessException {

    public CampaignNotFoundException(String message) {
        super(ErrorCode.CAMPAIGN_NOT_FOUND, message);
    }
}
