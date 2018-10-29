package com.foxpify.luckywheel.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class ErrorLogHandler<T> implements Handler<AsyncResult<T>> {
    private Consumer<Throwable> logFunc;

    public ErrorLogHandler(Logger logger, Level level, String msg, Object... params) {
        this(t -> logger.log(level, msg, params, t));
    }

    public ErrorLogHandler(Consumer<Throwable> logFunc) {
        this.logFunc = logFunc;
    }

    public ErrorLogHandler(Logger logger) {
        this(logger::error);
    }

    public ErrorLogHandler(Logger logger, String msg) {
        this(logger, Level.ERROR, msg);
    }

    @Override
    public void handle(AsyncResult<T> event) {
        if (event.failed()) {
            logFunc.accept(event.cause());
        }
    }
}
