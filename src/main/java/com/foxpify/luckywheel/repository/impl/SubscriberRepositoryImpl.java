package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.repository.SubscriberRepository;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import com.foxpify.vertxorm.repository.query.Query;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;

public class SubscriberRepositoryImpl  extends AbstractCrudRepository<Long, Subscriber> implements SubscriberRepository {

    @Inject
    public SubscriberRepositoryImpl(SQLClient sqlClient) {
        Config<Long, Subscriber> conf = new Config.Builder<Long, Subscriber>("subscriber", Subscriber::new)
                .pk("id", Subscriber::getId, Subscriber::setId, true)
                .addField("shop_id", Subscriber::getShopId, Subscriber::setShopId)
                .addUuidField("campaign_id", Subscriber::getCampaignId, Subscriber::setCampaignId)
                .addField("full_name", Subscriber::getFullName, Subscriber::setFullName)
                .addField("email", Subscriber::getEmail, Subscriber::setEmail)
                .addField("discount_code", Subscriber::getDiscountCode, Subscriber::setDiscountCode)
                .addTimestampTzField("created_at", Subscriber::getCreatedAt, Subscriber::setCreatedAt)
                .build();
        init(sqlClient, conf);
    }

    @Override
    public void remove(Query<Subscriber> query, Handler<AsyncResult<Integer>> resultHandler) {
        sqlClient.updateWithParams("UPDATE \"subscriber\" SET \"deleted\" = TRUE WHERE " + query.getConditionSql(),
                query.getConditionParams(), ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture(ar.result().getUpdated()));
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }
}