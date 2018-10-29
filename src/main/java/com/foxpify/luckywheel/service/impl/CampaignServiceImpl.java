package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.BusinessException;
import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.handler.ResponseHandler;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Slice;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.util.Model;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.exception.ShopifyException;
import com.foxpify.shopifyapi.model.DiscountCode;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.foxpify.vertxorm.repository.query.QueryFactory.*;

@Singleton
public class CampaignServiceImpl implements CampaignService {
    private static final Logger logger = LogManager.getLogger(CampaignServiceImpl.class);
    private CampaignRepository campaignRepository;
    private ShopService shopService;
    private ShopifyClient shopifyClient;

    @Inject
    public CampaignServiceImpl(CampaignRepository campaignRepository, ShopService shopService, ShopifyClient shopifyClient) {
        this.campaignRepository = campaignRepository;
        this.shopService = shopService;
        this.shopifyClient = shopifyClient;
    }

    @Override
    public void getCampaign(UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler) {
        campaignRepository.find(campaignId, resultHandler);
    }

    @Override
    public void getCampaigns(Collection<UUID> campaignIds, Handler<AsyncResult<List<Campaign>>> resultHandler) {
        if (campaignIds.isEmpty()) {
            resultHandler.handle(Future.succeededFuture(Collections.emptyList()));
            return;
        }
        List<String> ids = campaignIds.stream().map(UUID::toString).collect(Collectors.toList());
        Query<Campaign> query = in("id", ids);
        campaignRepository.findAll(query, resultHandler);
    }

    @Override
    public void getCampaign(User user, UUID campaignId, Handler<AsyncResult<Optional<Campaign>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        campaignRepository.find(and(equal("id", campaignId.toString()), equal("shop_id", shopId)), resultHandler);
    }

    @Override
    public void getCampaigns(User user, Query<Campaign> filter, PageRequest pageRequest, Handler<AsyncResult<Page<Campaign>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        Query<Campaign> query = equal("shop_id", shopId);
        if (filter != null) {
            query = and(query, filter).orderBy(filter.orderBy());
        }
        campaignRepository.findAll(query, pageRequest, resultHandler);
    }

    @Override
    public void createCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        campaign.setShopId(user.principal().getLong("sub"));
        campaign.setId(UUID.randomUUID());
        OffsetDateTime now = OffsetDateTime.now();
        campaign.setCreatedAt(now);
        campaign.setUpdatedAt(now);
        if (campaign.getActive() == null) {
            campaign.setActive(true);
        }
        if (campaign.getStartedAt() == null) {
            campaign.setStartedAt(now);
        }
        checkRunningDate(campaign).compose(this::setupSlides).compose(campaignRepository::insert).setHandler(resultHandler);
    }

    private Future<Campaign> checkRunningDate(Campaign campaign) {
        if (!campaign.getActive()) {
            return Future.succeededFuture(campaign);
        }
        String start = campaign.getStartedAt().toString();
        Query<Campaign> query1 = and(lessThanOrEqualTo("started_at", start), raw("active = true"), or(isNull("completed_at"), greaterThan("completed_at", start)));
        Query<Campaign> query2 = greaterThanOrEqualTo("started_at", start);
        if (campaign.getCompletedAt() != null) {
            query2 = and(query2, lessThan("started_at", campaign.getCompletedAt().toString()));
        }
        Query<Campaign> query = and(equal("shop_id", campaign.getShopId()), raw("active = true"), or(query1, query2)).limit(1);
        return campaignRepository.find(query).map(campaignOpt -> {
            campaignOpt.ifPresent(c -> {
                throw new BusinessException(ErrorCode.OVERLAP_RUNNING_CAMPAIGN, "overlap running with campaign '" + c.getName() + "'");
            });
            return campaign;
        });
    }

    private Future<Campaign> setupSlides(Campaign campaign) {
        if (campaign.getSlices() == null || campaign.getSlices().isEmpty()) {
            return Future.succeededFuture(campaign);
        }
        for (int i = 0, n = campaign.getSlices().size(); i < n; ++i) {
            campaign.getSlices().get(i).setIndex(i);
        }
        Set<String> codes = campaign.getSlices().stream()
                .map(Slice::getDiscountCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (codes.isEmpty()) {
            return Future.succeededFuture(campaign);
        }
        return shopService.getShop(campaign.getShopId())
                .map(shop -> shopifyClient.newSession(shop.getShop(), shop.getAccessToken()))
                .compose(session -> {
                    List<Future> discountCodes = codes.stream()
                            .map(code -> session.searchDiscountCode(code, false).otherwise(t -> {
                                if (t instanceof ShopifyException) {
                                    throw new ValidateException(ErrorCode.INVALID_DISCOUNT_CODE, "discode code: '" + code + "' is invalid");
                                }
                                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "can't get discount code: '" + code + "'");
                            }))
                            .collect(Collectors.toList());
                    return CompositeFuture.all(discountCodes);
                })
                .map(discountCodes -> {
                    Map<String, Long> priceRules = new HashMap<>();
                    for (int i = 0; i < discountCodes.size(); ++i) {
                        DiscountCode discountCode = discountCodes.resultAt(i);
                        priceRules.put(discountCode.getCode(), discountCode.getPriceRuleId());
                    }
                    campaign.getSlices().forEach(slice -> slice.setPriceRuleId(priceRules.get(slice.getDiscountCode())));
                    return campaign;
                });
    }

    @Override
    public void updateCampaign(User user, Campaign campaign, Handler<AsyncResult<Campaign>> resultHandler) {
        getCampaign(user, campaign.getId()).compose(campaignOpt -> {
            Campaign origin = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaign.getId() + " is not found"));
            copyNonNull(campaign, origin);
            if (origin.getStartedAt() != null && origin.getCompletedAt() != null && !origin.getStartedAt().isBefore(origin.getCompletedAt())) {
                throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Campaign's startedAt must before completedAt");
            }
            origin.setUpdatedAt(OffsetDateTime.now());
            return checkRunningDate(origin).compose(this::setupSlides).compose(campaignRepository::update);
        }).setHandler(resultHandler);
    }

    private void copyNonNull(Campaign src, Campaign dest) {
        Model.copyNonNull(src::getName, dest::setName);
        Model.copyNonNull(src::getDescription, dest::setDescription);
        Model.copyNonNull(src::getActive, dest::setActive);
        Model.copyNonNull(src::getWinProbability, dest::setWinProbability);
        Model.copyNonNull(src::getStartedAt, dest::setStartedAt);
        Model.copyNonNull(src::getCompletedAt, dest::setCompletedAt);
        Model.copyNonNull(src::getSlices, dest::setSlices);
        Model.copyNonNull(src::getMetadata, dest::setMetadata);
    }

    @Override
    public void setActiveCampaign(User user, UUID campaignId, boolean active, Handler<AsyncResult<Campaign>> resultHandler) {
        getCampaign(user, campaignId).compose(campaignOpt -> {
            Campaign campaign = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign " + campaignId + " is not found"));
            campaign.setActive(active);
            return checkRunningDate(campaign).compose(campaignRepository::update);
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
