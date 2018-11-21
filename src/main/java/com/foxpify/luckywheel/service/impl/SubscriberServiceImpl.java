package com.foxpify.luckywheel.service.impl;

import com.foxpify.luckywheel.exception.CampaignNotFoundException;
import com.foxpify.luckywheel.exception.SlideNotFoundException;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Slice;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.luckywheel.model.request.SubscribeRequest;
import com.foxpify.luckywheel.repository.SubscriberRepository;
import com.foxpify.luckywheel.service.CampaignService;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.service.SubscriberService;
import com.foxpify.luckywheel.util.ObjectHolder;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.util.Page;
import com.foxpify.vertxorm.util.PageRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.foxpify.vertxorm.repository.query.QueryFactory.and;
import static com.foxpify.vertxorm.repository.query.QueryFactory.equal;

@Singleton
public class SubscriberServiceImpl implements SubscriberService {
    private static final char[] CODE_SEED = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Query<Subscriber> notDeleted = equal("deleted", false);
    private CampaignService campaignService;
    private ShopService shopService;
    private SubscriberRepository subscriberRepository;
    private ShopifyClient shopifyClient;


    @Inject
    public SubscriberServiceImpl(CampaignService campaignService, ShopService shopService, SubscriberRepository subscriberRepository, ShopifyClient shopifyClient) {
        this.campaignService = campaignService;
        this.shopService = shopService;
        this.subscriberRepository = subscriberRepository;
        this.shopifyClient = shopifyClient;
    }

    @Override
    public void subscribe(SubscribeRequest subscribeRequest, Handler<AsyncResult<Slice>> resultHandler) {
        ObjectHolder<Campaign> holder = new ObjectHolder<>();
        campaignService.getCampaign(subscribeRequest.getCampaignId()).map(campaignOpt -> {
            Campaign campaign = campaignOpt.orElseThrow(() -> new CampaignNotFoundException("campaign: " + subscribeRequest.getCampaignId() + " is not found"));
            List<Slice> slices = campaign.getSlices();
            if (slices == null || slices.isEmpty()) {
                throw new SlideNotFoundException("campaign: " + campaign.getId() + " has no slices");
            }
            holder.setValue(campaign);
            return spinWheel(campaign);
        })
                .compose(slice -> {
                    Future<Subscriber> subscriber = createSubscriber(subscribeRequest, holder.getValue(), slice.getDiscountCode());
                    if (slice.getPriceRuleId() != null && slice.getAuto() != null && slice.getAuto()) {
                        return subscriber.compose(sub -> generateDiscountCode(holder.getValue().getShopId(), slice));
                    }
                    return subscriber.map(slice);
                })
                .setHandler(resultHandler);
    }

    private String randCode() {
        StringBuilder code = new StringBuilder("WL");
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int i = 0; i < 10; ++i) {
            code.append(CODE_SEED[rand.nextInt(CODE_SEED.length)]);
        }
        return code.toString();
    }

    private Future<Slice> generateDiscountCode(Long shopId, Slice slice) {
        String code = randCode();
        return shopService.getShop(shopId)
                .map(shop -> shopifyClient.newSession(shop.getShop(), shop.getAccessToken()))
                .compose(session -> session.createDiscountCode(slice.getPriceRuleId(), code, 1))
                .map(discount -> {
                    slice.setDiscountCode(code);
                    return slice;
                });
    }

    private Future<Subscriber> createSubscriber(SubscribeRequest subscribeRequest, Campaign campaign, String discountCode) {
        Subscriber subscriber = new Subscriber();
        subscriber.setCampaignId(subscribeRequest.getCampaignId());
        subscriber.setCampaignName(campaign.getName());
        subscriber.setFullName(subscribeRequest.getFullName());
        subscriber.setEmail(subscribeRequest.getEmail());
        subscriber.setCreatedAt(OffsetDateTime.now());
        subscriber.setShopId(campaign.getShopId());
        subscriber.setDiscountCode(discountCode);
        return subscriberRepository.save(subscriber);
    }

    private Float calculateWinProb(Campaign campaign) {
        if (campaign.getWinProbability() != null) {
            return campaign.getWinProbability();
        }
        List<Slice> slices = campaign.getSlices();
        boolean populationPercent = slices.stream().map(Slice::getProbability).anyMatch(Objects::isNull);
        if (populationPercent) {
            long luckySlicesNum = slices.stream().filter(s -> s.getDiscountCode() != null).count();
            return luckySlicesNum / (float) slices.size();
        }
        float totalScore = 0;
        float luckyScore = 0;
        for (Slice s : slices) {
            if (s.getDiscountCode() != null) {
                luckyScore += s.getProbability();
            }
            totalScore += s.getProbability();
        }
        return luckyScore / totalScore;
    }

    private Slice spinWheel(Campaign campaign) {
        List<Slice> slices = campaign.getSlices();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Float winProb = calculateWinProb(campaign);
        if (winProb == null) {
            return slices.get(random.nextInt(slices.size()));
        } else if (random.nextFloat() >= winProb) {
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
    @SuppressWarnings("unchecked")
    public void getSubscribers(User user, Query<Subscriber> filter, PageRequest pageRequest, Handler<AsyncResult<Page<Subscriber>>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        Query<Subscriber> query;
        if (filter != null) {
            query = and(equal("shop_id", shopId), notDeleted, filter).orderBy(filter.orderBy());
        } else {
            query = and(equal("shop_id", shopId), notDeleted);
        }
        subscriberRepository.findAll(query, pageRequest, resultHandler);
    }

    @Override
    public void removeSubscriber(User user, String email, Handler<AsyncResult<Void>> resultHandler) {
        Long shopId = user.principal().getLong("sub");
        Query<Subscriber> query = and(equal("shop_id", shopId), notDeleted);
        subscriberRepository.remove(query).map(v -> (Void) null).setHandler(resultHandler);
    }
}
