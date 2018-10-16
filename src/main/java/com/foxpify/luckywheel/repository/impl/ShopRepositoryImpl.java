package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.Shop;
import com.foxpify.luckywheel.repository.ShopRepository;
import com.foxpify.shopifyapi.model.User;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import com.foxpify.vertxorm.repository.query.Query;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.foxpify.vertxorm.repository.query.QueryFactory.and;
import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

public class ShopRepositoryImpl extends AbstractCrudRepository<Long, Shop> implements ShopRepository {
    private String returningEnitySql;
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
        returningEnitySql = " returning " + conf.getColumnNames().stream().map(c -> "\"" + c + "\"").collect(Collectors.joining(","));
    }

    @Override
    public void nextId(Handler<AsyncResult<Long>> resultHandler) {
        sqlClient.querySingle("select nextval('shop_id_seq')", ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture(ar.result().getLong(0)));
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void remove(Long shopId, Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler) {
        query = and(query, equal("id", shopId), equal("deleted", false));
        remove(query, resultHandler);
    }

    @Override
    public void remove(String shop, Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler) {
        query = and(query, equal("shop", shop), equal("deleted", false));
        remove(query, resultHandler);
    }

    @Override
    public void remove(Query<Shop> query, Handler<AsyncResult<Optional<Shop>>> resultHandler) {
        String sql = where("update shop set deleted = true", query) + returningEnitySql;
        sqlClient.querySingleWithParams(sql,
                query.getConditionParams(), toEntity(resultHandler));
    }
}
