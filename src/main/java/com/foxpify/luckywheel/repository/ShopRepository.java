package com.foxpify.luckywheel.repository;

import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.vertxorm.repository.CrudRepository;
import com.foxpify.vertxorm.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface ShopRepository extends CrudRepository<Long, Shop> {
    default Future<Long> nextId() {
        return Futures.toFuture(this::nextId);
    }

    void nextId(Handler<AsyncResult<Long>> resultHandler);
}
