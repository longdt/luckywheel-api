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

import static com.foxpify.vertxorm.repository.query.QueryFactory.*;

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
    public void getCampaign(User user, UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        campaignRepository.find(and(equal("id", campaignId.toString()), equal("shop_id", shopId)), resultHandler);
    }

    @Override
    public void createCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        campaign.setShopId(user.principal().getLong("sub"));
        campaign.setId(UUID.randomUUID());
        OffsetDateTime now = OffsetDateTime.now();
        campaign.setCreatedAt(now);
        campaign.setUpdatedAt(now);
        setupSlideIndex(campaign);
        campaignRepository.save(campaign, resultHandler);
    }

    private void setupSlideIndex(Campaign campaign) {
        if (campaign.getSlides() != null) {
            for (int i = 0, n = campaign.getSlides().size(); i < n; ++i) {
                campaign.getSlides().get(i).setIndex(i);
            }
        }
    }

    @Override
    public void updateCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        setupSlideIndex(campaign);
    }

    @Override
    public void deleteCampaign(User user, UUID campaignId, Handler<AsyncResult<Campaign>> resultHandler) {

    }

}
