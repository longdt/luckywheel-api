package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.exception.InvalidHmacException;
import com.foxpify.luckywheel.exception.ShopTokenNotFoundException;
import com.foxpify.luckywheel.model.entity.ShopToken;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.entity.Wheel;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.luckywheel.repository.ShopTokenRepository;
import com.foxpify.luckywheel.repository.WheelRepository;
import com.foxpify.luckywheel.service.LuckyWheelService;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.ErrorLogHandler;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.model.Asset;
import com.foxpify.shopifyapi.model.Theme;
import com.foxpify.shopifyapi.util.Futures;
import com.foxpify.vertxorm.repository.EntityNotFoundException;
import com.foxpify.vertxorm.repository.query.Query;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.foxpify.vertxorm.repository.query.QueryFactory.and;
import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

public class LuckyWheelServiceImpl implements LuckyWheelService {
    private static final Logger logger = LogManager.getLogger(LuckyWheelServiceImpl.class);
    private ShopifyClient shopifyClient;
    private ShopTokenRepository shopTokenRepository;
    private WheelRepository wheelRepository;
    private AsyncLoadingCache<String, ShopToken> tokenByShopCache;
    private String uninstalledUrl;

    @Inject
    public LuckyWheelServiceImpl(AppConf appConf, ShopifyClient shopifyClient, ShopTokenRepository shopTokenRepository, WheelRepository wheelRepository) {
        this.uninstalledUrl = appConf.getHttpHost() + appConf.getContextPath() + Constant.UNINSTALL_ENDPOINT;
        this.shopifyClient = shopifyClient;
        this.shopTokenRepository = shopTokenRepository;
        this.wheelRepository = wheelRepository;

        tokenByShopCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.HOURS)
                .buildAsync((shop, executor) -> {
                    Future<ShopToken> tokenFuture = shopTokenRepository.querySingle(byShop(shop))
                            .map(tokenOpt -> tokenOpt.orElseThrow(() -> new ShopTokenNotFoundException("DB shop: " + shop)));
                    return Futures.toCompletableFuture(tokenFuture);
                });
    }

    private Query<ShopToken> byShop(String shop) {
        return and(equal("shop", shop), equal("deleted", false));
    }

    @Override
    public String install(String shop, String authUrl) {
        return shopifyClient.getInstallUrl(shop, APP_SCOPES, authUrl);
    }

    @Override
    public void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<Void>> resultHandler) {
        if (shopifyClient.verifyRequest(hmac, params)) {
            Futures.toFuture(tokenByShopCache.get(shop))
                    .recover(t ->
                            shopifyClient.requestToken(shop, code).compose(authToken -> {
                                ShopToken token = new ShopToken(shop, authToken);
                                OffsetDateTime now = OffsetDateTime.now();
                                token.setCreatedAt(now);
                                token.setUpdatedAt(now);
                                Future<ShopToken> saveFuture = shopTokenRepository.save(token);
                                shopifyClient.newSession(shop, token.getAccessToken())
                                        .createWebhook(Constant.UNINSTALLED_TOPIC, uninstalledUrl,
                                                new ErrorLogHandler<>(logger, Level.ERROR, "can't create webhook for shop {}", shop));
                                return saveFuture;
                            }).map(shopToken -> {
                                tokenByShopCache.put(shopToken.getShop(), CompletableFuture.completedFuture(shopToken));
                                return shopToken;
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
        shopTokenRepository.querySingle(byShop(shop))
                .map(tokenOpt -> tokenOpt.orElse(null))
                .compose(shopToken -> {
                    if (shopToken == null) {
                        return Future.<Void>succeededFuture();
                    }
                    shopToken.setDeleted(true);
                    removeWheelGui(shopToken.getShop(), shopToken.getAccessToken(), new ErrorLogHandler<>(logger, Level.ERROR, "can't remove gui"));
                    return shopTokenRepository.save(shopToken).map(token -> {
                        tokenByShopCache.synchronous().invalidate(token.getShop());
                        return (Void) null;
                    });
                }).setHandler(resultHandler);
    }

    @Override
    public void createWheelGui(String shop, Handler<AsyncResult<Void>> resultHandler) {
        Futures.toFuture(tokenByShopCache.get(shop))
                .map(t -> shopifyClient.newSession(t.getShop(), t.getAccessToken()))
                .compose(session ->
                        session.findThemes().map(themes -> themes.stream().filter(theme -> theme.getRole() == Theme.Role.MAIN).findFirst().orElse(null))
                                .compose(theme -> session.findAsset(theme.getId(), "layout/theme.liquid"))
                                .compose(asset -> updateAsset(session, asset))
                                .map(asset -> (Void) null)
                ).setHandler(resultHandler);
    }

    public void removeWheelGui(String shop, String accessToken, Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public void createWheel(Long tokenId, Wheel wheel, Handler<AsyncResult<Wheel>> resultHandler) {

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

    @Override
    public void spinWheel(SpinRequest spinReq, Handler<AsyncResult<Slide>> resultHandler) {
        wheelRepository.find(spinReq.getWheelId()).map(wheel -> {
            List<Slide> slides = wheel.orElseThrow(() -> new EntityNotFoundException("wheel " + spinReq.getWheelId() + " isn't exists")).getSlides();
            int totalGravity = 0;
            int[] cumulatives = new int[slides.size()];
            for (int i = 0; i < slides.size(); ++i) {
                cumulatives[i] = totalGravity;
                totalGravity += slides.get(i).getProbability();
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
