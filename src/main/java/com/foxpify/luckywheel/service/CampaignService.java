package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.shopifyapi.util.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Optional;
import java.util.UUID;

public interface CampaignService {
    default Future<Optional<Campaign>> getCampaign(UUID campaignId) {
        return Futures.toFuture(this::getCampaign, campaignId);
    }

    void getCampaign(UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler);

    void createCampaign(Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler);
}
