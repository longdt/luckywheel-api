package com.foxpify.luckywheel.service;

import com.foxpify.luckywheel.handler.ResponseHandler;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.shopifyapi.util.Futures;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignService {
    default Future<Optional<Campaign>> getCampaign(UUID campaignId) {
        return Futures.toFuture(this::getCampaign, campaignId);
    }

    void getCampaign(UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler);

    default Future<List<Campaign>> getCampaigns(Collection<UUID> campaignIds) {
        return Futures.toFuture(this::getCampaigns, campaignIds);
    }

    void getCampaigns(Collection<UUID> campaignIds, Handler<AsyncResult<List<Campaign>>> resultHandler);

    default Future<Optional<Campaign>> getCampaign(User user, UUID campaignId){
        return Futures.toFuture(this::getCampaign, user, campaignId);
    }

    void getCampaign(User user, UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler);

    void getCampaigns(User user, Query<Campaign> filter, PageRequest pageRequest, ResponseHandler<Page<Campaign>> resultHandler);

    void createCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler);

    void updateCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler);

    default void activateCampaign(User user, UUID campaignId, Handler<AsyncResult<Campaign>> resultHandler) {
        setActiveCampaign(user, campaignId, true, resultHandler);
    }

    default void deactivateCampaign(User user, UUID campaignId, Handler<AsyncResult<Campaign>> resultHandler) {
        setActiveCampaign(user, campaignId, false, resultHandler);
    }

    void setActiveCampaign(User user, UUID campaignId, boolean active, Handler<AsyncResult<Campaign>> resultHandler);

    void deleteCampaign(User user, UUID campaignId, Handler<AsyncResult<Void>> resultHandler);

}
