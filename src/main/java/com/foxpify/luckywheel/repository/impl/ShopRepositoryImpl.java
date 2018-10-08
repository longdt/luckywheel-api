package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.luckywheel.repository.ShopRepository;
import com.foxpify.shopifyapi.model.User;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;

public class ShopRepositoryImpl extends AbstractCrudRepository<Long, Shop> implements ShopRepository {

    @Inject
    public ShopRepositoryImpl(SQLClient sqlClient) {
        Config<Long, Shop> conf = new Config.Builder<Long, Shop>("shop", Shop::new)
                .pk("id", Shop::getId, Shop::setId, true)
                .addField("shop", Shop::getShop, Shop::setShop)
                .addField("deleted", Shop::isDeleted, Shop::setDeleted)
                .addField("access_token", Shop::getAccessToken, Shop::setAccessToken)
                .addField("scope", Shop::getScope, Shop::setScope)
                .addField("expires_in", Shop::getExpiresIn, Shop::setExpiresIn)
                .addField("associated_user_scope", Shop::getAssociatedUserScope, Shop::setAssociatedUserScope)
                .addJsonField("associated_user", Shop::getAssociatedUser, Shop::setAssociatedUser, User.class)
                .addTimestampTzField("created_at", Shop::getCreatedAt, Shop::setCreatedAt)
                .addTimestampTzField("updated_at", Shop::getUpdatedAt, Shop::setUpdatedAt)
                .build();
        init(sqlClient, conf);
    }
}
