package com.foxpify.luckywheel.conf;

import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.repository.ShopRepository;
import com.foxpify.luckywheel.repository.SubscriberRepository;
import com.foxpify.luckywheel.repository.impl.CampaignRepositoryImpl;
import com.foxpify.luckywheel.repository.impl.ShopRepositoryImpl;
import com.foxpify.luckywheel.repository.impl.SubscriberRepositoryImpl;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.InstallService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.luckywheel.service.impl.CampaignServiceImpl;
import com.foxpify.luckywheel.service.impl.InstallServiceImpl;
import com.foxpify.luckywheel.service.impl.ShopServiceImpl;
import com.foxpify.luckywheel.service.impl.SubscriberServiceImpl;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.client.ShopifyClientImpl;
import com.foxpify.shopifyapi.util.Futures;
import dagger.Module;
import dagger.Provides;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;

@Module
public class AppModule {
    private static final Logger logger = LogManager.getLogger(AppModule.class);
    private Vertx vertx;

    public AppModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Singleton
    @Provides
    public AppConf provideAppConf() {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "conf/app.yml"));

        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(store));
        Future<JsonObject> future = ConfigRetriever.getConfigAsFuture(retriever);
        Future<AppConf> appConf = future.map(jsonObject -> jsonObject.mapTo(AppConf.class).setJsonObject(jsonObject));
        return Futures.join(appConf);
    }

    @Singleton
    @Provides
    SQLClient provideSQLClient(AppConf appConf) {
        return PostgreSQLClient.createShared(vertx, appConf.getDatasource());
    }

    @Singleton
    @Provides
    ShopifyClient provideShopifyClient(AppConf appConf) {
        return new ShopifyClientImpl(vertx, appConf.getAppKey(), appConf.getAppSecret());
    }

    @Singleton
    @Provides
    JWTAuthHandler provideJWTAuthHandler(AppConf appConf) {
        JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions(appConf.getJWTAuthOptions()));
        return JWTAuthHandler.create(jwtAuth);
    }

    @Provides
    InstallService provideInstallService(InstallServiceImpl installService) {
        return installService;
    }

    @Provides
    ShopService provideShopService(ShopServiceImpl shopService) {
        return shopService;
    }

    @Provides
    ShopRepository provideShopRepository(ShopRepositoryImpl shopRepository) {
        return shopRepository;
    }

    @Provides
    CampaignService provideCampaignService(CampaignServiceImpl campaignService) {
        return campaignService;
    }

    @Provides
    CampaignRepository provideCampaignRepository(CampaignRepositoryImpl campaignRepository) {
        return campaignRepository;
    }

    @Provides
    SubscriberService provideSubscriberService(SubscriberServiceImpl subscriberService) {
        return subscriberService;
    }

    @Provides
    SubscriberRepository provideSubscriberRepository(SubscriberRepositoryImpl subscriberRepository) {
        return subscriberRepository;
    }

    @Singleton
    @Provides
    Vertx provideVertx() {
       return vertx;
    }
}
