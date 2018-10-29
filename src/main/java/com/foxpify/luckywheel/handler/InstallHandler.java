package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.service.InstallService;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.Responses;
import com.foxpify.luckywheel.validate.WebhookValidator;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class InstallHandler {
    private static final Logger logger = LogManager.getLogger(InstallHandler.class);
    private InstallService installService;
    private WebhookValidator validator;
    private String authUrl;
    private String adminUrl;

    @Inject
    public InstallHandler(AppConf appConf, InstallService installService, WebhookValidator validator) {
        this.authUrl = appConf.getHttpHost() + appConf.getContextPath() + Constant.AUTH_ENDPOINT;
        this.adminUrl = appConf.getAdminUrl();
        this.installService = installService;
        this.validator = validator;
    }

    public void install(RoutingContext routingContext) {
        String shop = routingContext.request().getParam("shop");
        if (shop == null) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Missing shop parameter. Please add ?shop=your-development-shop.myshopify.com to your request");
        }
        int state = ThreadLocalRandom.current().nextInt();
        String redirectUrl = installService.install(shop, authUrl) + "&state=" + state;
        routingContext.addCookie(Cookie.cookie("state", String.valueOf(state)));
        Responses.redirect(routingContext, redirectUrl);
    }

    public void uninstall(RoutingContext routingContext) {
        validator.validate(routingContext);
        String shop = routingContext.request().getHeader("X-Shopify-Shop-Domain");
        String topic = routingContext.request().getHeader("X-Shopify-Topic");
        if (Constant.UNINSTALLED_TOPIC.equals(topic) || Constant.REDACT_SHOP_TOPIC.equals(topic)) {
            logger.info("shop {} request {} shop data: {}", shop, topic, routingContext.getBodyAsString());
            installService.uninstall(shop);
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
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Required parameters missing");
        }
        Cookie stateCookie = routingContext.getCookie("state");
        if (stateCookie == null || !state.equals(stateCookie.getValue())) {
            throw new ValidateException(ErrorCode.ORIGIN_CANT_BE_VERIFIED, "Request origin cannot be verified");
        }
        installService.auth(shop, code, hmac, routingContext.request().params(), new ResponseHandler<String>(routingContext) {
            @Override
            public void success(String result) throws Throwable {
                Responses.redirect(routingContext, adminUrl + "?token=" + result);
            }
        });
    }

    public void deleteCustomer(RoutingContext routingContext) {
        validator.validate(routingContext);
        String shop = routingContext.request().getHeader("X-Shopify-Shop-Domain");
        String topic = routingContext.request().getHeader("X-Shopify-Topic");
        logger.info("shop {} request delete customer data: {}", shop, routingContext.getBodyAsString());
        Responses.ok(routingContext);
    }

    public void viewCustomer(RoutingContext routingContext) {
        validator.validate(routingContext);
        String shop = routingContext.request().getHeader("X-Shopify-Shop-Domain");
        String topic = routingContext.request().getHeader("X-Shopify-Topic");
        logger.info("shop {} request view customer data: {}", shop, routingContext.getBodyAsString());
        Responses.ok(routingContext);
    }
}
