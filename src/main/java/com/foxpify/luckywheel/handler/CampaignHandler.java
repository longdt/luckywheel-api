package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.CampaignValidator;
import com.foxpify.shopifyapi.util.Json;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CampaignHandler {
    private CampaignService campaignService;
    private CampaignValidator validator;

    @Inject
    public CampaignHandler(CampaignService campaignService, CampaignValidator validator) {
        this.campaignService = campaignService;
        this.validator = validator;
    }

    public void createCampaign(RoutingContext routingContext) {
        Campaign campaign = Json.decodeValue(routingContext.getBody(), Campaign.class);
        validator.validate(campaign);
        campaignService.createCampaign(routingContext.user(), campaign, new ResponseHandler<Campaign>(routingContext) {

            @Override
            public void success(Campaign result) throws Throwable {
                Responses.ok(routingContext, result);
            }
        });
    }

    public void getCampaign(RoutingContext routingContext) {
        UUID campaignId;
        try {
            campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param");
        }
        campaignService.getCampaign(routingContext.user(), campaignId, new ResponseHandler<Optional<Campaign>>(routingContext) {

            @Override
            public void success(Optional<Campaign> result) throws Throwable {
                Responses.ok(routingContext, result.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaignId + " is not found")));
            }
        });
    }
}
