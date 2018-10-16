package com.foxpify.luckywheel.repository;

import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.vertxorm.repository.CrudRepository;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Optional;

public interface ShopRepository extends CrudRepository<Long, Shop> {
    default Future<Long> nextId() {
        return Futures.toFuture(this::nextId);
    }

    void nextId(Handler<AsyncResult<Long>> resultHandler);

    default Future<Optional<Shop>> remove(Long shopId, Query<Shop> query) {
        return Futures.toFuture(this::remove, shopId, query);
    }

    void remove(Long shopId, Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler);

    default Future<Optional<Shop>> remove(String shop, Query<Shop> query) {
        return Futures.toFuture(this::remove, shop, query);
    }

    void remove(String shop, Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler);

    default Future<Optional<Shop>> remove(Query<Shop> query) {
        return Futures.toFuture(this::remove, query);
    }

    void remove(Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler);
}
