package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Shop;
import io.vertx.core.Future;

public interface ShopService {
    Future<Shop> getShop(String shop);

    Future<Shop> getShop(Long shopId);

    Future<Shop> createShop(Shop shop);

    Future<Void> removeShop(String shop);
}
