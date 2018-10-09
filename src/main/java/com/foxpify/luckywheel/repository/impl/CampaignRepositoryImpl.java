package com.foxpify.luckywheel.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class CampaignRepositoryImpl extends AbstractCrudRepository<UUID, Campaign> implements CampaignRepository {
    @Inject
    public CampaignRepositoryImpl(SQLClient sqlClient) {
        Config<UUID, Campaign> conf = new Config.Builder<UUID, Campaign>("campaign", Campaign::new)
                .pk("id", Campaign::getId, Campaign::setId)
                .pkConverter(UUID::toString, UUID::fromString)
                .addField("shop_id", Campaign::getShopId, Campaign::setShopId)
                .addField("name", Campaign::getName, Campaign::setName)
                .addField("description", Campaign::getDescription, Campaign::setDescription)
                .addField("active", Campaign::getActive, Campaign::setActive)
                .addField("win_probability", Campaign::getWinProbability, Campaign::setWinProbability)
                .addTimestampTzField("created_at", Campaign::getCreatedAt, Campaign::setCreatedAt)
                .addTimestampTzField("updated_at", Campaign::getUpdatedAt, Campaign::setUpdatedAt)
                .addTimestampTzField("started_at", Campaign::getStartedAt, Campaign::setStartedAt)
                .addTimestampTzField("completed_at", Campaign::getCompletedAt, Campaign::setCompletedAt)
                .addJsonField("slides", Campaign::getSlides, Campaign::setSlides, new TypeReference<List<Slide>>() {
                })
                .addJsonField("metadata", Campaign::getMetadata, Campaign::setMetadata, JsonObject.class)
                .build();
        init(sqlClient, conf);
    }
}
