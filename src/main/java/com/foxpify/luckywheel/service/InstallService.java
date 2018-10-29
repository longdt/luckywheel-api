package com.foxpify.luckywheel.service;

import com.foxpify.shopifyapi.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;

public interface InstallService {
    String APP_SCOPES = "write_script_tags,write_price_rules";

    String install(String shop, String authUrl);

    void auth(String shop, String code, String hmac, MultiMap params, Handler<AsyncResult<String>> resultHandler);

    default Future<Void> uninstall(String shop) {
        return Futures.toFuture(this::uninstall, shop);
    }

    void uninstall(String shop, Handler<AsyncResult<Void>> resultHandler);

    default Future<Void> setupTheme(String shop, Long themeId) {
        return Futures.toFuture(this::setupTheme, shop, themeId);
    }

    void setupTheme(String shop, Long themeId, Handler<AsyncResult<Void>> resultHandler);
}
