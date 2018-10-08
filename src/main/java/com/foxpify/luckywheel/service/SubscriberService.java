package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface SubscriberService {
    void subscribe(SubscribeRequest subscribeRequest, Handler<AsyncResult<Slide>> resultHandler);
}
