package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.ShopToken;
import com.foxpify.luckywheel.repository.ShopTokenRepository;
import com.foxpify.shopifyapi.model.User;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;

public class ShopTokenRepositoryImpl extends AbstractCrudRepository<String, ShopToken> implements ShopTokenRepository {

    @Inject
    public ShopTokenRepositoryImpl(SQLClient sqlClient) {
        Config<String, ShopToken> conf = new Config.Builder<String, ShopToken>("shop_token", ShopToken::new)
                .pk("shop", ShopToken::getShop, ShopToken::setShop)
                .addField("access_token", ShopToken::getAccessToken, ShopToken::setAccessToken)
                .addField("scope", ShopToken::getScope, ShopToken::setScope)
                .addField("expires_in", ShopToken::getExpiresIn, ShopToken::setExpiresIn)
                .addField("associated_user_scope", ShopToken::getAssociatedUserScope, ShopToken::setAssociatedUserScope)
                .addJsonField("associated_user", ShopToken::getAssociatedUser, ShopToken::setAssociatedUser, User.class)
                .addTimestampTzField("created_date", ShopToken::getCreatedDate, ShopToken::setCreatedDate)
                .addTimestampTzField("updated_date", ShopToken::getUpdatedDate, ShopToken::setUpdatedDate)
                .build();
        init(sqlClient, conf);
    }
}
