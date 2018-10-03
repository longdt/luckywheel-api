package com.foxpify.luckywheel.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.entity.Wheel;
import com.foxpify.luckywheel.model.entity.Wheel;
import com.foxpify.luckywheel.repository.WheelRepository;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class WheelRepositoryImpl extends AbstractCrudRepository<UUID, Wheel> implements WheelRepository {
    @Inject
    public WheelRepositoryImpl(SQLClient sqlClient) {
        Config<UUID, Wheel> conf = new Config.Builder<UUID, Wheel>("wheel", Wheel::new)
                .pk("id", Wheel::getId, Wheel::setId)
                .addField("name", Wheel::getName, Wheel::setName)
                .addField("active", Wheel::getActive, Wheel::setActive)
                .addField("win_probability", Wheel::getWinProbability, Wheel::setWinProbability)
                .addTimestampTzField("created_at", Wheel::getCreatedAt, Wheel::setCreatedAt)
                .addTimestampTzField("updated_at", Wheel::getUpdatedAt, Wheel::setUpdatedAt)
                .addTimestampTzField("started_at", Wheel::getStartedAt, Wheel::setStartedAt)
                .addTimestampTzField("completed_at", Wheel::getCompletedAt, Wheel::setCompletedAt)
                .addJsonField("slides", Wheel::getSlides, Wheel::setSlides, new TypeReference<>() {
                })
                .build();
        init(sqlClient, conf);
    }
}
