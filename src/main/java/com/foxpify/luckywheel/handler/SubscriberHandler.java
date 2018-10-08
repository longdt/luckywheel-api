package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.SubscribeRequestValidator;
import com.foxpify.shopifyapi.util.Json;
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
        UUID campaignId = UUID.fromString(routingContext.request().getParam("campaignId"));
        SubscribeRequest subscribeRequest = Json.decodeValue(routingContext.getBody(), SubscribeRequest.class);
        subscribeRequest.setCampaignId(campaignId);
        validator.validate(subscribeRequest);
        subscriberService.subscribe(subscribeRequest, new ResponseHandler<Slide>(routingContext) {
            @Override
            public void success(Slide result) throws Throwable {
                Responses.ok(routingContext, result);
            }
        });
    }
}
