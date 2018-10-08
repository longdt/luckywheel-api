package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.model.Asset;
import com.foxpify.shopifyapi.model.Theme;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    public void createCampaign(Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        campaignRepository.save(campaign, resultHandler);
    }

}
