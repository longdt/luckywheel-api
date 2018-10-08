package com.foxpify.luckywheel.conf;

import com.foxpify.luckywheel.handler.CampaignHandler;
import com.foxpify.luckywheel.handler.InstallHandler;
import com.foxpify.luckywheel.handler.SubscriberHandler;
import dagger.Component;
import dagger.Provides;
import io.vertx.ext.web.handler.JWTAuthHandler;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    InstallHandler installHandler();

    CampaignHandler campaignHandler();

    SubscriberHandler subscriberHandler();

    JWTAuthHandler jWTAuthHandler();

    AppConf appConf();
}
