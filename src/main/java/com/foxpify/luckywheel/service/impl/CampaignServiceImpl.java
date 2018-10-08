package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.service.CampaignService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CampaignServiceImpl implements CampaignService {
    private static final Logger logger = LogManager.getLogger(InstallServiceImpl.class);
    private CampaignRepository campaignRepository;

    @Inject
    public CampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public void getCampaign(UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler) {
        campaignRepository.find(campaignId, resultHandler);
    }

    @Override
    public void createCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        campaign.setShopId(user.principal().getLong("sub"));
        campaign.setId(UUID.randomUUID());
        OffsetDateTime now = OffsetDateTime.now();
        campaign.setCreatedAt(now);
        campaign.setUpdatedAt(now);
        campaignRepository.save(campaign, resultHandler);
    }

    @Override
    public void updateCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {

    }

    @Override
    public void deleteCampaign(User user, UUID campaignId, Handler<AsyncResult<Campaign>> resultHandler) {

    }

}
