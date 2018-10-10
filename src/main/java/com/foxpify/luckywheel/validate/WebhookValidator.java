package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.InvalidHmacException;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.shopifyapi.client.ShopifyClient;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebhookValidator implements Validator<RoutingContext> {
    private ShopifyClient shopifyClient;

    @Inject
    public WebhookValidator(ShopifyClient shopifyClient) {
        this.shopifyClient = shopifyClient;
    }

    @Override
    public RoutingContext validate(RoutingContext routingContext) throws ValidateException {
        String hmac = routingContext.request().getHeader("X-Shopify-Hmac-SHA256");
        if (!shopifyClient.verifyData(hmac, routingContext.getBody())) {
            throw new InvalidHmacException("HMAC validation failed");
        }
        return routingContext;
    }
}