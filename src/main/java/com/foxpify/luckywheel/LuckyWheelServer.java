package com.foxpify.luckywheel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.foxpify.luckywheel.conf.AppComponent;
import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.conf.AppModule;
import com.foxpify.luckywheel.conf.DaggerAppComponent;
import com.foxpify.luckywheel.handler.ExceptionHandler;
import com.foxpify.luckywheel.handler.LuckyWheelHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LuckyWheelServer {
    private static final Logger logger = LogManager.getLogger(LuckyWheelServer.class);
    private Vertx vertx;
    private HttpServer server;
    private int port;

    public LuckyWheelServer() {
        vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        init();
    }

    private void init() {
        Router router = Router.router(vertx);
        AppModule module = new AppModule(vertx);
        AppConf conf = module.provideAppConf();
        port = conf.getHttpPort();
        AppComponent creator = DaggerAppComponent.builder().appModule(module).build();
        LuckyWheelHandler luckyWheelHandler = creator.createLuckyWheelHandler();
        router.route().handler(LoggerHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.mountSubRouter(conf.getContextPath(), createRouter(luckyWheelHandler));
        router.route("/static/*").handler(StaticHandler.create("www"));
        router.route().failureHandler(ExceptionHandler::handle);
        server.requestHandler(router::accept);
    }

    private Router createRouter(LuckyWheelHandler luckyWheelHandler) {
        Router router = Router.router(vertx);
        router.get("/install").handler(luckyWheelHandler::install);
        router.get("/auth").handler(luckyWheelHandler::auth);
        router.post("/wheels/:wheelId/spin").handler(luckyWheelHandler::spinWheel);
        return router;
    }

    public void start() {
        server.listen(port, event -> {
            if (event.succeeded()) {
                logger.info("Server started on {} port", event.result().actualPort());
            } else {
                logger.error("Cant start server on {} port", port, event.cause());
            }
        });
    }

    public void stop() {
        server.close(event -> {
            if (event.succeeded()) {
                logger.info("Server was stopped");
            } else {
                logger.error("Cant stop server", event.cause());
            }
        });
    }

    public static void main(String[] args) {
        Json.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Json.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        new LuckyWheelServer().start();
    }
}
