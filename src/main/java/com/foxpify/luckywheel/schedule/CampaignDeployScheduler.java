package com.foxpify.luckywheel.schedule;

import com.foxpify.luckywheel.model.entity.Campaign;
import io.vertx.core.Future;

public interface CampaignDeployScheduler {

    Future<Void> schedule(Campaign campaign);

    Future<Void> activate(Campaign campaign);

    Future<Void> deactivate(Campaign campaign);

    Future<Void> remove(Campaign campaign);

    void shutdown();
}
