package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;
import static com.foxpify.luckywheel.validate.Validator.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CampaignValidator implements Validator<Campaign> {
    private SliceValidator sliceValidator;
    @Inject
    public CampaignValidator(SliceValidator sliceValidator) {
        this.sliceValidator = sliceValidator;
    }

    @Override
    public Campaign validate(Campaign campaign) throws ValidateException {
        requireNonEmpty(campaign.getName(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Campaign must has a name"));
        if (campaign.getSlices() != null) {
            campaign.getSlices().forEach(sliceValidator::validate);
        }
        if (campaign.getStartedAt() != null && campaign.getCompletedAt() != null && !campaign.getStartedAt().isBefore(campaign.getCompletedAt())) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Campaign's startedAt must before completedAt");
        }
        return campaign;
    }

    public Campaign validateUpdate(Campaign campaign) throws ValidateException {
        if (campaign.getName() != null) {
            requireNonEmpty(campaign.getName(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Campaign must has a name"));
        }
        if (campaign.getSlices() != null) {
            campaign.getSlices().forEach(sliceValidator::validate);
        }
        return campaign;
    }
}
