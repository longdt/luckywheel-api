package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.repository.SubscriberRepository;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
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
                .addTimestampTzField("created_at", Subscriber::getCreatedAt, Subscriber::setCreatedAt)
                .build();
        init(sqlClient, conf);
    }
}