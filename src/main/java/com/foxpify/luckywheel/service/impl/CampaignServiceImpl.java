package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.handler.ResponseHandler;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.util.Model;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
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

import static com.foxpify.vertxorm.repository.query.QueryFactory.and;
import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

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
        campaignRepository.find(campaignId).map(campaign -> campaign.map(c -> {
            if (c.getSlides() != null) {
                c.getSlides().forEach(s -> s.setDiscountCode(null));
            }
            return c;
        })).setHandler(resultHandler);
    }

    @Override
    public void getCampaign(User user, UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        campaignRepository.find(and(equal("id", campaignId.toString()), equal("shop_id", shopId)), resultHandler);
    }

    @Override
    public void getCampaigns(User user, PageRequest pageRequest, ResponseHandler<Page<Campaign>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        Query<Campaign> query = equal("shop_id", shopId);
        campaignRepository.findAll(query, pageRequest, resultHandler);
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
        getCampaign(user, campaign.getId()).compose(campaignOpt -> {
            Campaign origin = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaign.getId() + " is not found"));
            copyNonNull(campaign, origin);
            setupSlideIndex(origin);
            origin.setUpdatedAt(OffsetDateTime.now());
            return campaignRepository.update(origin);
        }).setHandler(resultHandler);
    }

    private void copyNonNull(Campaign src, Campaign dest) {
        Model.copyNonNull(src::getName, dest::setName);
        Model.copyNonNull(src::getDescription, dest::setDescription);
        Model.copyNonNull(src::getActive, dest::setActive);
        Model.copyNonNull(src::getWinProbability, dest::setWinProbability);
        Model.copyNonNull(src::getStartedAt, dest::setStartedAt);
        Model.copyNonNull(src::getCompletedAt, dest::setCompletedAt);
        Model.copyNonNull(src::getSlides, dest::setSlides);
        Model.copyNonNull(src::getMetadata, dest::setMetadata);
    }

    @Override
    public void setActiveCampaign(User user, UUID campaignId, boolean active, Handler<AsyncResult<Campaign>> resultHandler) {
        getCampaign(user, campaignId).compose(campaignOpt -> {
            Campaign campaign = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaignId + " is not found"));
            campaign.setActive(active);
            return campaignRepository.save(campaign);
        }).setHandler(resultHandler);
    }

    @Override
    public void deleteCampaign(User user, UUID campaignId, Handler<AsyncResult<Void>> resultHandler) {
        getCampaign(user, campaignId).compose(campaignOpt -> {
            campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaignId + " is not found"));
            return campaignRepository.delete(campaignId);
        }).setHandler(resultHandler);
    }

}
