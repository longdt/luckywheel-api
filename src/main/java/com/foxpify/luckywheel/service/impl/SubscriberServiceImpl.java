package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.exception.SlideNotFoundException;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Slice;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.luckywheel.repository.SubscriberRepository;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.luckywheel.util.ObjectHolder;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import static com.foxpify.vertxorm.repository.query.QueryFactory.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Singleton
public class SubscriberServiceImpl implements SubscriberService {
    private CampaignService campaignService;
    private SubscriberRepository subscriberRepository;

    @Inject
    public SubscriberServiceImpl(CampaignService campaignService, SubscriberRepository subscriberRepository) {
        this.campaignService = campaignService;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public void subscribe(SubscribeRequest subscribeRequest, Handler<AsyncResult<Slice>> resultHandler) {
        ObjectHolder<Campaign> holder = new ObjectHolder<>();
        campaignService.getCampaign(subscribeRequest.getCampaignId()).compose(campaignOpt -> {
            Campaign campaign = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign: " + subscribeRequest.getCampaignId() + " is not found"));
            holder.setValue(campaign);
            return createSubscriber(subscribeRequest, campaign);
        }).map(sub -> spinWheel(holder.getValue())).setHandler(resultHandler);
    }

    private Future<Subscriber> createSubscriber(SubscribeRequest subscribeRequest, Campaign campaign) {
        Subscriber subscriber = new Subscriber();
        subscriber.setCampaignId(subscribeRequest.getCampaignId());
        subscriber.setFullName(subscribeRequest.getFullName());
        subscriber.setEmail(subscribeRequest.getEmail());
        subscriber.setCreatedAt(OffsetDateTime.now());
        subscriber.setShopId(campaign.getShopId());
        return subscriberRepository.save(subscriber);
    }

    private Slice spinWheel(Campaign campaign) {
        List<Slice> slices = campaign.getSlices();
        if (slices == null || slices.isEmpty()) {
            throw new SlideNotFoundException("campaign: " + campaign.getId() + " has no slices");
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextFloat() >= campaign.getWinProbability()) {
            List<Slice> badSlices = slices.stream().filter(s -> s.getDiscountCode() == null).collect(Collectors.toList());
            if (!badSlices.isEmpty()) {
                return badSlices.get(random.nextInt(badSlices.size()));
            }
        }
        List<Slice> luckySlices = slices.stream().filter(s -> s.getDiscountCode() != null).collect(Collectors.toList());
        if (luckySlices.isEmpty()) {
            return slices.get(random.nextInt(slices.size()));
        }
        int totalGravity = 0;
        int[] cumulatives = new int[luckySlices.size()];
        for (int i = 0; i < luckySlices.size(); ++i) {
            cumulatives[i] = totalGravity;
            totalGravity += luckySlices.get(i).getProbability();
        }
        int randGrav = random.nextInt(totalGravity);
        int index = 0;
        for (int i = cumulatives.length - 1; i >= 0; --i) {
            if (cumulatives[i] <= randGrav) {
                index = i;
                break;
            }
        }
        return luckySlices.get(index);
    }

    @Override
    public void getSubscribers(User user, PageRequest pageRequest, Handler<AsyncResult<Page<Subscriber>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        Query<Subscriber> query = equal("shop_id", shopId);
        subscriberRepository.findAll(query, pageRequest, resultHandler);
    }
}
