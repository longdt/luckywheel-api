package com.foxpify.luckywheel.conf;

import com.foxpify.luckywheel.handler.LuckyWheelHandler;
import com.foxpify.luckywheel.repository.ShopTokenRepository;
import com.foxpify.luckywheel.repository.WheelRepository;
import com.foxpify.luckywheel.repository.impl.ShopTokenRepositoryImpl;
import com.foxpify.luckywheel.repository.impl.WheelRepositoryImpl;
import com.foxpify.luckywheel.service.LuckyWheelService;
import com.foxpify.luckywheel.service.impl.LuckyWheelServiceImpl;
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
import io.vertx.ext.sql.SQLClient;
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
    LuckyWheelHandler provideLuckyWheelHandler(AppConf appConf, LuckyWheelService luckyWheelService)  {
        return new LuckyWheelHandler(appConf, luckyWheelService);
    }

    @Singleton
    @Provides
    LuckyWheelService provideLuckyWheelService(ShopifyClient shopifyClient, ShopTokenRepository shopTokenRepository, WheelRepository wheelRepository) {
        return new LuckyWheelServiceImpl(shopifyClient, shopTokenRepository, wheelRepository);
    }

    @Singleton
    @Provides
    ShopTokenRepository provideShopTokenRepository(SQLClient sqlClient) {
        return new ShopTokenRepositoryImpl(sqlClient);
    }

    @Singleton
    @Provides
    WheelRepository provideSlideRepository(SQLClient sqlClient) {
        return new WheelRepositoryImpl(sqlClient);
    }

    @Singleton
    @Provides
    ShopifyClient provideShopifyClient(AppConf appConf) {
        return new ShopifyClientImpl(vertx, appConf.getAppKey(), appConf.getAppSecret());
    }

    @Singleton
    @Provides
    Vertx provideVertx() {
       return vertx;
    }
}
