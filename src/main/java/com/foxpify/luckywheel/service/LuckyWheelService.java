package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.entity.Wheel;
import com.foxpify.luckywheel.model.request.SpinRequest;
import com.foxpify.shopifyapi.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

public interface LuckyWheelService {
    String APP_SCOPES = "read_themes,write_themes";

    String install(String shop, String authUrl);

    void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<Void>> resultHandler);

    default Future<Void> uninstall(String shop, String hmac, Buffer body) {
        return Futures.toFuture(this::uninstall, shop, hmac, body);
    }

    void uninstall(String shop, String hmac, Buffer body, Handler<AsyncResult<Void>> resultHandler);

    default Future<Void> createWheelGui(String shop) {
        return Futures.toFuture(this::createWheelGui, shop);
    }

    void createWheelGui(String shop, Handler<AsyncResult<Void>> resultHandler);

    default Future<Void> removeWheelGui(String shop, String accessToken) {
        return Futures.toFuture(this::removeWheelGui, shop, accessToken);
    }

    void removeWheelGui(String shop, String accessToken, Handler<AsyncResult<Void>> resultHandler);

    void createWheel(Long tokenId, Wheel wheel, Handler<AsyncResult<Wheel>> resultHandler);

    void spinWheel(SpinRequest spinReq, Handler<AsyncResult<Slide>> resultHandler);
}
