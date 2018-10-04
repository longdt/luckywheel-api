package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.luckywheel.service.LuckyWheelService;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.shopifyapi.util.Json;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyWheelHandler {
    private LuckyWheelService luckyWheelService;
    private String authUrl;
    private String adminUrl;

    @Inject
    public LuckyWheelHandler(AppConf appConf, LuckyWheelService luckyWheelService) {
        this.authUrl = appConf.getHttpHost() + appConf.getContextPath() + Constant.AUTH_ENDPOINT;
        this.adminUrl = appConf.getAdminUrl();
        this.luckyWheelService = luckyWheelService;
    }

    public void install(RoutingContext routingContext) {
        String shop = routingContext.request().getParam("shop");
        if (shop == null) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING, "Missing shop parameter. Please add ?shop=your-development-shop.myshopify.com to your request");
        }
        int state = ThreadLocalRandom.current().nextInt();
        String redirectUrl = luckyWheelService.install(shop, authUrl) + "&state=" + state;
        routingContext.addCookie(Cookie.cookie("state", String.valueOf(state)));
        Responses.redirect(routingContext, redirectUrl);
    }

    public void uninstall(RoutingContext routingContext) {
        String hmac = routingContext.request().getHeader("X-Shopify-Hmac-SHA256");
        String shop = routingContext.request().getHeader("X-Shopify-Shop-Domain");
        String topic = routingContext.request().getHeader("X-Shopify-Topic");
        if (Constant.UNINSTALLED_TOPIC.equals(topic)) {
            luckyWheelService.uninstall(shop, hmac, routingContext.getBody());
            Responses.ok(routingContext);
        } else {
            Responses.badRequest(routingContext);
        }
    }

    public void auth(RoutingContext routingContext) {
        String shop = routingContext.request().getParam("shop");
        String code = routingContext.request().getParam("code");
        String hmac = routingContext.request().getParam("hmac");
        String state = routingContext.request().getParam("state");
        if (shop == null || code == null || hmac == null || state == null) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING, "Required parameters missing");
        }
        Cookie stateCookie = routingContext.getCookie("state");
        if (stateCookie == null || !state.equals(stateCookie.getValue())) {
            throw new ValidateException(ErrorCode.ORIGIN_CANT_BE_VERIFIED, "Request origin cannot be verified");
        }
        luckyWheelService.auth(shop, code, hmac, routingContext.request().params(), new ResponseHandler<Void>(routingContext) {
            @Override
            public void success(Void result) throws Throwable {
                Responses.redirect(routingContext, adminUrl);
            }
        });
    }

    public void createWheel(RoutingContext routingContext) {

    }

    public void spinWheel(RoutingContext routingContext) {
        UUID wheelId = UUID.fromString(routingContext.request().getParam("wheelId"));
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
