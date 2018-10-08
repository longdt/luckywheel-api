package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.InvalidHmacException;
import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.InstallService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.ErrorLogHandler;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.model.Asset;
import com.foxpify.shopifyapi.model.Theme;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InstallServiceImpl implements InstallService {
    private static final Logger logger = LogManager.getLogger(InstallServiceImpl.class);
    private ShopifyClient shopifyClient;
    private ShopService shopService;

    private String uninstalledUrl;

    @Inject
    public InstallServiceImpl(AppConf appConf, ShopifyClient shopifyClient, ShopService shopService) {
        this.uninstalledUrl = appConf.getHttpHost() + appConf.getContextPath() + Constant.UNINSTALL_ENDPOINT;
        this.shopifyClient = shopifyClient;
        this.shopService = shopService;
    }

    @Override
    public String install(String shop, String authUrl) {
        return shopifyClient.getInstallUrl(shop, APP_SCOPES, authUrl);
    }

    @Override
    public void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<Void>> resultHandler) {
        if (shopifyClient.verifyRequest(hmac, params)) {
            shopService.getShop(shop)
                    .recover(t ->
                            shopifyClient.requestToken(shop, code).compose(authToken -> {
                                shopifyClient.newSession(shop, authToken.getAccessToken())
                                        .createWebhook(Constant.UNINSTALLED_TOPIC, uninstalledUrl,
                                                new ErrorLogHandler<>(logger, Level.ERROR, "can't create webhook for shop {}", shop));
                                return shopService.createShop(new Shop(shop, authToken));
                            })
                    );
            Future.<Void>succeededFuture().setHandler(resultHandler);
        } else {
            throw new InvalidHmacException("HMAC validation failed");
        }
    }

    @Override
    public void uninstall(String shop, String hmac, Buffer body, Handler<AsyncResult<Void>> resultHandler) {
        if (!shopifyClient.verifyData(hmac, body)) {
            throw new InvalidHmacException("HMAC validation failed");
        }
        shopService.removeShop(shop).setHandler(resultHandler);
    }

    public void createWheelGui(String shop, Handler<AsyncResult<Void>> resultHandler) {
        shopService.getShop(shop)
                .map(t -> shopifyClient.newSession(t.getShop(), t.getAccessToken()))
                .compose(session ->
                        session.findThemes().map(themes -> themes.stream().filter(theme -> theme.getRole() == Theme.Role.MAIN).findFirst().orElse(null))
                                .compose(theme -> session.findAsset(theme.getId(), "layout/theme.liquid"))
                                .compose(asset -> updateAsset(session, asset))
                                .map(asset -> (Void) null)
                ).setHandler(resultHandler);
    }

    public void removeWheelGui(String shop, String accessToken, Handler<AsyncResult<Void>> resultHandler) {
        Session session = shopifyClient.newSession(shop, accessToken);
        session.findThemes().map(themes -> themes.stream().filter(theme -> theme.getRole() == Theme.Role.MAIN).findFirst().orElse(null))
                .compose(theme -> session.findAsset(theme.getId(), "layout/theme.liquid"))
                .map(asset -> (Void) null).setHandler(resultHandler);
        //TODO REMOVE GUI

    }


    private Future<Asset> updateAsset(Session session, Asset asset) {
        String content = asset.getValue();
        if (content.contains("<div id=\"luckywheel-div\"")) {
            return Future.succeededFuture();
        }
        String wheelHtml = "<div class=\"page-container\" id=\"PageContainer\">\n" +
                "    <div id=\"luckywheel-div\" style=\"transition: all 0.7s ease-out 0s; z-index: 2147483647; transform: translateX(0px);\">\n" +
                "        <iframe id=\"luckywheel-iframe\" frameborder=\"0\" src=\"https://longdt.foxpify.com/static/lucky_wheel.htm\" style=\"width: 800px;height: 600px;\"></iframe>\n" +
                "    </div>";
        content = content.replace("<div class=\"page-container\" id=\"PageContainer\">", wheelHtml);
        asset.setValue(content);
        return session.updateAsset(asset);
    }
}
