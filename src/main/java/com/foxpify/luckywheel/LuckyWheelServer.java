package com.foxpify.luckywheel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.foxpify.luckywheel.conf.AppComponent;
import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.conf.AppModule;
import com.foxpify.luckywheel.conf.DaggerAppComponent;
import com.foxpify.luckywheel.handler.CampaignHandler;
import com.foxpify.luckywheel.handler.ExceptionHandler;
import com.foxpify.luckywheel.handler.InstallHandler;
import com.foxpify.luckywheel.handler.SubscriberHandler;
import com.foxpify.luckywheel.util.Constant;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
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
        router.route().handler(LoggerHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().failureHandler(ExceptionHandler::handle);

        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(vertx)).build();
        router.route(Constant.ADMIN_SUBROUTE_ENDPOINT + "/*").handler(appComponent.jWTAuthHandler());
        initInstallRouter(router, appComponent.installHandler());
        initCampaignRouter(router, appComponent.campaignHandler());
        initSubscriberRouter(router, appComponent.subscriberHandler());
        router.route("/static/*").handler(StaticHandler.create("www"));

        Router mainRouter = Router.router(vertx);
        AppConf conf = appComponent.appConf();
        port = conf.getHttpPort();
        mainRouter.mountSubRouter(conf.getContextPath(), router);
        server.requestHandler(mainRouter::accept);
    }

    private void initInstallRouter(Router router, InstallHandler installHandler) {
        router.get(Constant.INSTALL_ENDPOINT).handler(installHandler::install);
        router.post(Constant.UNINSTALL_ENDPOINT).handler(installHandler::uninstall);
        router.get(Constant.AUTH_ENDPOINT).handler(installHandler::auth);
    }

    private void initCampaignRouter(Router router, CampaignHandler campaignHandler) {
        router.post(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.CREAT_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::createCampaign);
        router.get(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.GET_CAMPAIGNS_ENDPOINT)
                .handler(campaignHandler::getCampaigns);
        router.get(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.GET_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::getCampaign);
        router.patch(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.UPDATE_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::updateCampaign);
        router.post(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.ACTIVATE_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::activateCampaign);
        router.post(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.DEACTIVATE_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::deactivateCampaign);
        router.delete(Constant.ADMIN_SUBROUTE_ENDPOINT + Constant.DELETE_CAMPAIGN_ENDPOINT)
                .handler(campaignHandler::deleteCampaign);
    }

    private void initSubscriberRouter(Router router, SubscriberHandler subscriberHandler) {
        router.post(Constant.SUBSCRIBE_ENDPOINT).handler(subscriberHandler::subscribe);
        router.get(Constant.GET_SUBSCRIBERS_ENDPOINT).handler(subscriberHandler::getSubscribers);
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
