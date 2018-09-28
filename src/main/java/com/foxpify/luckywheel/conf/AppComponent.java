package com.foxpify.luckywheel.conf;

import com.foxpify.luckywheel.handler.LuckyWheelHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    LuckyWheelHandler createLuckyWheelHandler();
}
