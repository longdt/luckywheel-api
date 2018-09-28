package com.foxpify.luckywheel.repository.impl;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.repository.SlideRepository;
import com.foxpify.vertxorm.repository.impl.AbstractCrudRepository;
import com.foxpify.vertxorm.repository.impl.Config;
import io.vertx.ext.sql.SQLClient;

import javax.inject.Inject;

public class SlideRepositoryImpl extends AbstractCrudRepository<Long, Slide> implements SlideRepository {
    @Inject
    public SlideRepositoryImpl(SQLClient sqlClient) {
        Config<Long, Slide> conf = new Config.Builder<Long, Slide>("slide", Slide::new)
                .pk("slide_id", Slide::getSlideId, Slide::setSlideId)
                .addField("wheel_id", Slide::getWheelId, Slide::setWheelId)
                .addField("label", Slide::getLabel, Slide::setLabel)
                .addField("discount_code", Slide::getDiscountCode, Slide::setDiscountCode)
                .addField("discount_code_id", Slide::getDiscountCodeId, Slide::setDiscountCodeId)
                .addField("gravity", Slide::getGravity, Slide::setGravity)
                .build();
        init(sqlClient, conf);
    }
}
