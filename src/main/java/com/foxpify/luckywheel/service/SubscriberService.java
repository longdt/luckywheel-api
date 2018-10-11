package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Slice;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;

public interface SubscriberService {
    void subscribe(SubscribeRequest subscribeRequest, Handler<AsyncResult<Slice>> resultHandler);

    void getSubscribers(User user, Query<Subscriber> filter, PageRequest pageRequest, Handler<AsyncResult<Page<Subscriber>>> resultHandler);
}
