package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.CampaignValidator;
import com.foxpify.shopifyapi.util.Json;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        User user = routingContext.user();
        campaign.setShopId(user.principal().getLong("shopId"));
        validator.validate(campaign);
        campaignService.createCampaign(campaign, new ResponseHandler<Campaign>(routingContext) {

            @Override
            public void success(Campaign result) throws Throwable {
                Responses.ok(routingContext, result);
            }
        });
    }
}
