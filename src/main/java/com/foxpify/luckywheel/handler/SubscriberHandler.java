package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.SubscribeRequestValidator;
import com.foxpify.shopifyapi.util.Json;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class SubscriberHandler {
    private SubscriberService subscriberService;
    private SubscribeRequestValidator validator;

    @Inject
    public SubscriberHandler(SubscriberService subscriberService, SubscribeRequestValidator validator) {
        this.subscriberService = subscriberService;
        this.validator = validator;
    }

    public void subscribe(RoutingContext routingContext) {
        SubscribeRequest subscribeRequest;
        try {
            UUID campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
            subscribeRequest = Json.decodeValue(routingContext.getBody(), SubscribeRequest.class);
            subscribeRequest.setCampaignId(campaignId);
        } catch (DecodeException e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require subscriber request json object", e);
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "require valid campaignId param", e);
        }

        validator.validate(subscribeRequest);
        subscriberService.subscribe(subscribeRequest, new ResponseHandler<Slide>(routingContext) {
            @Override
            public void success(Slide result) throws Throwable {
                Responses.ok(routingContext, result);
            }
        });
    }

    public void getSubscribers(RoutingContext routingContext) {
        try {
            int page = Integer.parseInt(routingContext.request().getParam("page"));
            int size = Integer.parseInt(routingContext.request().getParam("size"));
            if (page <= 0 || size <= 0) {
                throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Page index must start from 1 and size must > 0");
            }

            subscriberService.getSubscribers(routingContext.user(), new PageRequest(page, size), new ResponseHandler<Page<Subscriber>>(routingContext) {
                @Override
                public void success(Page<Subscriber> result) {
                    Responses.ok(routingContext, result);
                }
            });
        } catch (NumberFormatException e) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "page=" + routingContext.request().getParam("page")
                    + " size=" + routingContext.request().getParam("size"), e);
        }
    }
}
