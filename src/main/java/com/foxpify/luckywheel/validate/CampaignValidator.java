package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CampaignValidator implements Validator<Campaign> {

    @Inject
    public CampaignValidator() {
    }

    @Override
    public Campaign validate(Campaign campaign) throws ValidateException {
        return null;
    }
}
