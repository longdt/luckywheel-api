package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.ShopTokenNotFoundException;
import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.luckywheel.repository.ShopRepository;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.util.ErrorLogHandler;
import com.foxpify.shopifyapi.util.Futures;
import com.foxpify.vertxorm.repository.query.Query;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.vertx.core.Future;
import org.apache.logging.log4j.Level;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.foxpify.vertxorm.repository.query.QueryFactory.and;
import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

@Singleton
public class ShopServiceImpl implements ShopService {
    private ShopRepository shopRepository;
    private AsyncLoadingCache<String, Shop> tokenByShopCache;
    private static final Query<Shop> NOT_DELETED = equal("deleted", false);

    @Inject
    public ShopServiceImpl(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
        tokenByShopCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.HOURS)
                .buildAsync((shop, executor) -> {
                    Future<Shop> tokenFuture = shopRepository.querySingle(byShop(shop))
                            .map(tokenOpt -> tokenOpt.orElseThrow(() -> new ShopTokenNotFoundException("DB shop: " + shop)));
                    return Futures.toCompletableFuture(tokenFuture);
                });
    }

    private Query<Shop> byShop(String shop) {
        return and(equal("shop", shop), NOT_DELETED);
    }

    private Query<Shop> byId(Long shopId) {
        return and(equal("id", shopId), NOT_DELETED);
    }

    @Override
    public Future<Shop> getShop(String shop) {
        return Futures.toFuture(tokenByShopCache.get(shop));
    }

    @Override
    public Future<Shop> getShop(Long shopId) {
        return shopRepository.querySingle(byId(shopId))
                .map(tokenOpt -> tokenOpt.orElseThrow(() -> new ShopTokenNotFoundException("DB shop: " + shopId)));
    }

    @Override
    public Future<Shop> createShop(Shop shop) {
        OffsetDateTime now = OffsetDateTime.now();
        shop.setCreatedAt(now);
        shop.setUpdatedAt(now);
        return shopRepository.save(shop).map(shopToken -> {
            tokenByShopCache.put(shopToken.getShop(), CompletableFuture.completedFuture(shopToken));
            return shopToken;
        });
    }

    @Override
    public Future<Void> removeShop(String shop) {
        return getShop(shop).otherwise(t -> null)
                .compose(s -> {
                    if (s == null) {
                        return Future.<Void>succeededFuture();
                    }
                    s.setDeleted(true);
                    return shopRepository.save(s).map(sh -> {
                        tokenByShopCache.synchronous().invalidate(sh.getShop());
                        return (Void) null;
                    });
                });
    }
}