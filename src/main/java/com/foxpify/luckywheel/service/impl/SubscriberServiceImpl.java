package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.model.entity.Slide;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.vertxorm.repository.EntityNotFoundException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class SubscriberServiceImpl implements SubscriberService {
    private CampaignService campaignService;

    @Inject
    public SubscriberServiceImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public void subscribe(SubscribeRequest subscribeRequest, Handler<AsyncResult<Slide>> resultHandler) {
        campaignService.getCampaign(subscribeRequest.getCampaignId()).map(campaign -> {
            List<Slide> slides = campaign.orElseThrow(() -> new EntityNotFoundException("Campaign " + subscribeRequest.getCampaignId() + " isn't exists")).getSlides();
            int totalGravity = 0;
            int[] cumulatives = new int[slides.size()];
            for (int i = 0; i < slides.size(); ++i) {
                cumulatives[i] = totalGravity;
                totalGravity += slides.get(i).getProbability();
            }
            int randGrav = ThreadLocalRandom.current().nextInt(totalGravity);
            int index = 0;
            for (int i = cumulatives.length - 1; i >= 0; --i) {
                if (cumulatives[i] <= randGrav) {
                    index = i;
                    break;
                }
            }
            return slides.get(index);
        }).setHandler(resultHandler);
    }
}
