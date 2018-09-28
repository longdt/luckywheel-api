package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.RequiredParametersMissingException;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.luckywheel.service.LuckyWheelService;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.shopifyapi.util.Json;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;

public class LuckyWheelHandler {
    public static String AUTH_ENPOINT = "/luckywheel/auth";
    public static String INSTALL_ENPOINT = "/luckywheel/install";
    private LuckyWheelService luckyWheelService;
    private String authUrl;

    @Inject
    public LuckyWheelHandler(AppConf appConf, LuckyWheelService luckyWheelService) {
        this.authUrl = appConf.getHttpHost() + AUTH_ENPOINT;
        this.luckyWheelService = luckyWheelService;
    }

    public void install(RoutingContext routingContext) {
        String shop = routingContext.request().getParam("shop");
        if (shop == null) {
            throw new RequiredParametersMissingException("Missing shop parameter. Please add ?shop=your-development-shop.myshopify.com to your request");
        }
        Responses.redirect(routingContext, luckyWheelService.install(shop, authUrl));
    }

    public void auth(RoutingContext routingContext) {
        String shop = routingContext.request().getParam("shop");
        String code = routingContext.request().getParam("code");
        String hmac = routingContext.request().getParam("hmac");
        if (shop == null || code == null || hmac == null) {
            throw new RequiredParametersMissingException("shop: " + shop + "\tcode: " + code + "\thmac: " + hmac);
        }
        luckyWheelService.auth(shop, code, hmac, routingContext.request().params(), new ResponseHandler<Void>(routingContext) {
            @Override
            public void success(Void result) throws Throwable {
                Responses.ok(routingContext);
            }
        });
    }

    public void spinWheel(RoutingContext routingContext) {
        Long wheelId = Long.valueOf(routingContext.request().getParam("wheelId"));
        SpinRequest spinReq = Json.decodeValue(routingContext.getBody(), SpinRequest.class);
        spinReq.setWheelId(wheelId);
        luckyWheelService.spinWheel(spinReq, new ResponseHandler<Slide>(routingContext) {
            @Override
            public void success(Slide result) throws Throwable {
                Responses.ok(routingContext, result);
            }
        });
    }
}
