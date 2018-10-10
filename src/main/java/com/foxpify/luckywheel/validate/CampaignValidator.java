package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;
import static com.foxpify.luckywheel.validate.Validator.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CampaignValidator implements Validator<Campaign> {

    @Inject
    public CampaignValidator() {
    }

    @Override
    public Campaign validate(Campaign campaign) throws ValidateException {
        if (campaign.getSlices() != null) {
            requireNonEmpty(campaign.getSlices(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "slices must be null or none empty"));
        }
        return campaign;
    }
}
