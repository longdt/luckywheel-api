package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.CampaignValidator;
import com.foxpify.shopifyapi.util.Json;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.json.DecodeException;
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
        Campaign campaign;
        try {
            campaign = Json.decodeValue(routingContext.getBody(), Campaign.class);
        } catch (DecodeException e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require campaign json object", e);
        }
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
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }
        campaignService.getCampaign(routingContext.user(), campaignId, new ResponseHandler<Optional<Campaign>>(routingContext) {

            @Override
            public void success(Optional<Campaign> result) throws Throwable {
                Responses.ok(routingContext, result.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaignId + " is not found")));
            }
        });
    }

    public void getCampaigns(RoutingContext routingContext) {
        try {
            int page = Integer.parseInt(routingContext.request().getParam("page"));
            int size = Integer.parseInt(routingContext.request().getParam("size"));
            if (page <= 0 || size <= 0) {
                throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Page index must start from 1 and size must > 0");
            }

            campaignService.getCampaigns(routingContext.user(), new PageRequest(page, size), new ResponseHandler<Page<Campaign>>(routingContext) {
                @Override
                public void success(Page<Campaign> result) {
                    Responses.ok(routingContext, result);
                }
            });
        } catch (NumberFormatException e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "page=" + routingContext.request().getParam("page")
                    + " size=" + routingContext.request().getParam("size"), e);
        }
    }

    public void updateCampaign(RoutingContext routingContext) {
        Campaign campaign;
        try {
            UUID campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
            campaign = Json.decodeValue(routingContext.getBody(), Campaign.class);
            campaign.setId(campaignId);
        } catch (DecodeException e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require campaign json object", e);
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }
        validator.validateUpdate(campaign);
        campaignService.updateCampaign(routingContext.user(), campaign, new ResponseHandler<>(routingContext) {

            @Override
            public void success(Campaign result) {
                Responses.ok(routingContext, result);
            }
        });
    }

    public void activateCampaign(RoutingContext routingContext) {
        UUID campaignId;
        try {
            campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }
        campaignService.activateCampaign(routingContext.user(), campaignId, new ResponseHandler<>(routingContext) {

            @Override
            public void success(Campaign result) {
                Responses.ok(routingContext, result);
            }
        });
    }

    public void deactivateCampaign(RoutingContext routingContext) {
        UUID campaignId;
        try {
            campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }
        campaignService.deactivateCampaign(routingContext.user(), campaignId, new ResponseHandler<>(routingContext) {

            @Override
            public void success(Campaign result) {
                Responses.ok(routingContext, result);
            }
        });
    }

    public void deleteCampaign(RoutingContext routingContext) {
        UUID campaignId;
        try {
            campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }
        campaignService.deleteCampaign(routingContext.user(), campaignId, new ResponseHandler<Void>(routingContext) {

            @Override
            public void success(Void result) throws Throwable {
                Responses.ok(routingContext);
            }
        });
    }
}
