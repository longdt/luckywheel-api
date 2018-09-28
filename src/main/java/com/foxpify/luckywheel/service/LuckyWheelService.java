package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.vertxorm.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

public interface LuckyWheelService {
    String APP_SCOPES = "read_themes,write_themes";

    String install(String shop, String authUrl);

    void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<Void>> resultHandler);

    default Future<Void> createWheelGui(String shop) {
        return Futures.toFuture(this::createWheelGui, shop);
    }

    void createWheelGui(String shop, Handler<AsyncResult<Void>> resultHandler);

    void spinWheel(SpinRequest spinReq, Handler<AsyncResult<Slide>> resultHandler);
}
