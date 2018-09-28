package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.InvalidHmacException;
import com.foxpify.luckywheel.exception.ShopTokenNotFoundException;
import com.foxpify.luckywheel.model.entity.ShopToken;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.luckywheel.repository.ShopTokenRepository;
import com.foxpify.luckywheel.repository.SlideRepository;
import com.foxpify.luckywheel.service.LuckyWheelService;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.model.Asset;
import com.foxpify.shopifyapi.model.Theme;
import com.foxpify.shopifyapi.util.Futures;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

public class LuckyWheelServiceImpl implements LuckyWheelService {
    private ShopifyClient shopifyClient;
    private ShopTokenRepository shopTokenRepository;
    private SlideRepository slideRepository;
    private AsyncLoadingCache<String, Session> sessionCache;

    public LuckyWheelServiceImpl(ShopifyClient shopifyClient, ShopTokenRepository shopTokenRepository, SlideRepository slideRepository) {
        this.shopifyClient = shopifyClient;
        this.shopTokenRepository = shopTokenRepository;
        this.slideRepository = slideRepository;
        sessionCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.HOURS)
                .buildAsync((key, executor) -> Futures.toCompletableFuture(shopTokenRepository.find(key).map(tokenOpt -> {
                    ShopToken token = tokenOpt.orElseThrow(() -> new ShopTokenNotFoundException("shop: " + key));
                    return shopifyClient.newSession(token.getShop(), token.getAccessToken());
                })));


    }

    @Override
    public String install(String shop, String authUrl) {
        return shopifyClient.getInstallUrl(shop, APP_SCOPES, authUrl);
    }

    @Override
    public void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<Void>> resultHandler) {
        if (shopifyClient.verifyRequest(hmac, params)) {
            shopifyClient.requestToken(shop, code).compose(authToken -> {
                        ShopToken token = new ShopToken(shop, authToken);
                        OffsetDateTime now = OffsetDateTime.now();
                        token.setCreatedDate(now);
                        token.setUpdatedDate(now);
                        return shopTokenRepository.save(token);
                    }
            ).map(shopToken -> {
                Session session = shopifyClient.newSession(shopToken.getShop(), shopToken.getAccessToken());
                sessionCache.put(shopToken.getShop(), CompletableFuture.completedFuture(session));
                return session;
            }).compose(session -> createWheelGui(shop)).setHandler(resultHandler);
        } else {
            throw new InvalidHmacException("HMAC validation failed");
        }
    }

    @Override
    public void createWheelGui(String shop, Handler<AsyncResult<Void>> resultHandler) {
        Futures.toFuture(sessionCache.get(shop))
                .compose(session ->
                    session.findThemes().map(themes -> themes.stream().filter(theme -> theme.getRole() == Theme.Role.MAIN).findFirst().orElse(null))
                        .compose(theme -> session.findAsset(theme.getId(), "layout/theme.liquid"))
                        .compose(asset -> updateAsset(session, asset))
                        .map(asset -> (Void) null)
                ).setHandler(resultHandler);

    }

    Future<Asset> updateAsset(Session session, Asset asset) {
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

    @Override
    public void spinWheel(SpinRequest spinReq, Handler<AsyncResult<Slide>> resultHandler) {
        slideRepository.query(equal("wheel_id", spinReq.getWheelId())).map(slides -> {
            int totalGravity = 0;
            int[] cumulatives = new int[slides.size()];
            for (int i = 0; i < slides.size(); ++i) {
                cumulatives[i] = totalGravity;
                totalGravity += slides.get(i).getGravity();
            }
            int randGrav = ThreadLocalRandom.current().nextInt(totalGravity);
            int index = 0;
            for (int i = cumulatives.length - 1; i >= 0; --i) {
                if (cumulatives[i] <= randGrav) {
                    index = i;
                    break;
                }
            }
            return slides.get(index);
        }).setHandler(resultHandler);

    }
}
