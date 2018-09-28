package com.foxpify.luckywheel.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class ResponseHandler<T> implements Handler<AsyncResult<T>> {
    private RoutingContext routingContext;

    public ResponseHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(AsyncResult<T> event) {
        try {
            if (event.succeeded()) {
                success(event.result());
            } else {
                fail(event.cause());
            }
        } catch (Throwable e) {
            ExceptionHandler.handle(routingContext, e);
        }
    }

    public abstract void success(T result) throws Throwable;

    public void fail(Throwable cause) throws Throwable {
        ExceptionHandler.handle(routingContext, cause);
    }
}
