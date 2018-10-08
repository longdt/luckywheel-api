package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.vertxorm.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface ShopService {
    Future<Shop> getShop(String shop);

    Future<Shop> getShop(Long shopId);

    Future<Shop> createShop(Shop shop);

    default Future<Long> nextId() {
        return Futures.toFuture(this::nextId);
    }

    void nextId(Handler<AsyncResult<Long>> resultHandler);

    Future<Void> removeShop(String shop);
}
