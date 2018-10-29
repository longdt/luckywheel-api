package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.BusinessException;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.InvalidHmacException;
import com.foxpify.luckywheel.exception.ShopNotFoundException;
import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.luckywheel.service.InstallService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.ErrorLogHandler;
import com.foxpify.luckywheel.util.ObjectHolder;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.exception.ShopifyException;
import com.foxpify.shopifyapi.model.Asset;
import com.foxpify.shopifyapi.model.OAuthToken;
import com.foxpify.shopifyapi.model.Theme;
import com.foxpify.shopifyapi.util.Futures;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Singleton
public class InstallServiceImpl implements InstallService {
    private static final Logger logger = LogManager.getLogger(InstallServiceImpl.class);
    private ShopifyClient shopifyClient;
    private ShopService shopService;
    private JWTAuth jwtAuth;
    private String uninstalledUrl;
    private String wheelJsUrl;
    private final JWTOptions options;

    @Inject
    public InstallServiceImpl(AppConf appConf, ShopifyClient shopifyClient, ShopService shopService, JWTAuth jwtAuth) {
        this.uninstalledUrl = appConf.getHttpHost() + appConf.getContextPath() + Constant.UNINSTALL_ENDPOINT;
        this.wheelJsUrl = appConf.getWheelJsUrl();
        this.shopifyClient = shopifyClient;
        this.shopService = shopService;
        this.jwtAuth = jwtAuth;
        this.options = new JWTOptions().setExpiresInSeconds(appConf.getJwtExpiresInSeconds());
    }

    @Override
    public String install(String shop, String authUrl) {
        return shopifyClient.getInstallUrl(shop, APP_SCOPES, authUrl);
    }

    @Override
    public void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<String>> resultHandler) {
        if (shopifyClient.verifyRequest(hmac, params)) {
            shopService.getShop(shop)
                    .map(s -> {
                        updateAccessTokenIfNeed(s, shop, code).map(updatedShop -> {
                            if (updatedShop != null) {
                                setupShop(shop, updatedShop.getAccessToken());
                            }
                            return null;
                        });
                        return s.getId();
                    })
                    .recover(t -> {
                        if (!(t instanceof ShopNotFoundException)) {
                            return Future.failedFuture(t);
                        }
                        CompletableFuture<Long> shopIdFuture = Futures.toCompletableFuture(shopService::nextId);
                        CompositeFuture.all(Futures.toFuture(shopIdFuture), shopifyClient.requestToken(shop, code))
                                .compose(compositeFuture -> {
                                    Long shopId = compositeFuture.resultAt(0);
                                    OAuthToken authToken = compositeFuture.resultAt(1);
                                    setupShop(shop, authToken.getAccessToken());
                                    Shop s = new Shop(shop, authToken);
                                    s.setId(shopId);
                                    return shopService.createShop(s);
                                });
                        return Futures.toFuture(shopIdFuture);
                    }).map(shopId -> generateJwtToken(shopId, shop)).setHandler(resultHandler);
        } else {
            throw new InvalidHmacException("HMAC validation failed");
        }
    }

    private Future<Shop> updateAccessTokenIfNeed(Shop shop, String shopDomain, String code) {
        return shopifyClient.requestToken(shopDomain, code).compose(authToken -> {
            if (!shop.getAccessToken().equals(authToken.getAccessToken())) {
                shop.setAccessToken(authToken.getAccessToken());
                return shopService.updateShop(shop);
            }
            return Future.succeededFuture();
        });
    }

    private void setupShop(String shop, String accessToken) {
        Session session = shopifyClient.newSession(shop, accessToken);
        session.createWebhook(Constant.UNINSTALLED_TOPIC, uninstalledUrl,
                new ErrorLogHandler<>(logger, Level.ERROR, "can't create uninstall webhook for shop {}", shop));
        session.createScriptTag("onload", wheelJsUrl + "?shop=" + URLEncoder.encode(shop, StandardCharsets.UTF_8),
                new ErrorLogHandler<>(logger, Level.ERROR, "can't create setup theme webhook for shop {}", shop));
    }

    private String generateJwtToken(Long shopId, String shop) {
        JsonObject claims = new JsonObject().put("sub", shopId)
                .put("shop", shop);
        return jwtAuth.generateToken(claims, options);
    }

    @Override
    public void uninstall(String shop, Handler<AsyncResult<Void>> resultHandler) {
        ObjectHolder<String> accessTokenHolder = new ObjectHolder<>();
        shopService.getShop(shop)
                .compose(s -> {
                    String accessToken = s.getAccessToken();
                    accessTokenHolder.setValue(accessToken);
                    return testAccessToken(s.getShop(), accessToken);
                })
                .recover(t -> {
                    if (t instanceof ShopNotFoundException) {
                        return Future.succeededFuture();
                    } else if (t instanceof ShopifyException) {
                        return shopService.removeShop(shop, accessTokenHolder.getValue());
                    }
                    throw new BusinessException(ErrorCode.UNINSTALL_SHOP_ERROR, "Can't uninstall " + shop, t);
                }).setHandler(resultHandler);
    }

    private Future<Void> testAccessToken(String shop, String accessToken) {
        return shopifyClient.newSession(shop, accessToken).findShop("id").map((Void) null);
    }

    @Override
    public void setupTheme(String shop, Long themeId, Handler<AsyncResult<Void>> resultHandler) {

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
