package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.ShopToken;
import com.foxpify.luckywheel.repository.ShopTokenRepository;
import com.foxpify.shopifyapi.model.User;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;

public class ShopTokenRepositoryImpl extends AbstractCrudRepository<Long, ShopToken> implements ShopTokenRepository {

    @Inject
    public ShopTokenRepositoryImpl(SQLClient sqlClient) {
        Config<Long, ShopToken> conf = new Config.Builder<Long, ShopToken>("shop_token", ShopToken::new)
                .pk("id", ShopToken::getId, ShopToken::setId, true)
                .addField("shop", ShopToken::getShop, ShopToken::setShop)
                .addField("deleted", ShopToken::isDeleted, ShopToken::setDeleted)
                .addField("access_token", ShopToken::getAccessToken, ShopToken::setAccessToken)
                .addField("scope", ShopToken::getScope, ShopToken::setScope)
                .addField("expires_in", ShopToken::getExpiresIn, ShopToken::setExpiresIn)
                .addField("associated_user_scope", ShopToken::getAssociatedUserScope, ShopToken::setAssociatedUserScope)
                .addJsonField("associated_user", ShopToken::getAssociatedUser, ShopToken::setAssociatedUser, User.class)
                .addTimestampTzField("created_at", ShopToken::getCreatedAt, ShopToken::setCreatedAt)
                .addTimestampTzField("updated_at", ShopToken::getUpdatedAt, ShopToken::setUpdatedAt)
                .build();
        init(sqlClient, conf);
    }
}
