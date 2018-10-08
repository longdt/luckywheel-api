package com.foxpify.luckywheel.service;

import com.foxpify.shopifyapi.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

public interface InstallService {
    String APP_SCOPES = "read_themes,write_themes";

    String install(String shop, String authUrl);

    void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<String>> resultHandler);

    default Future<Void> uninstall(String shop, String hmac, Buffer body) {
        return Futures.toFuture(this::uninstall, shop, hmac, body);
    }

    void uninstall(String shop, String hmac, Buffer body, Handler<AsyncResult<Void>> resultHandler);
}
