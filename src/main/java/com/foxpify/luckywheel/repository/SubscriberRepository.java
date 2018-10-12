package com.foxpify.luckywheel.repository;

import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.shopifyapi.util.Futures;
import com.foxpify.vertxorm.repository.CrudRepository;
import com.foxpify.vertxorm.repository.query.Query;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface SubscriberRepository extends CrudRepository<Long, Subscriber> {
    default Future<Integer> remove(Query<Subscriber> query) {
        return Futures.toFuture(this::remove, query);
    }

    void remove(Query<Subscriber> query, Handler<AsyncResult<Integer>> resultHandler);
}
