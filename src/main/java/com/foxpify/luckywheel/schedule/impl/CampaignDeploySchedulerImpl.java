package com.foxpify.luckywheel.schedule.impl;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.repository.CampaignRepository;
import com.foxpify.luckywheel.schedule.CampaignDeployScheduler;
import com.foxpify.vertxorm.repository.query.Query;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.foxpify.vertxorm.repository.query.QueryFactory.*;

@Singleton
public class CampaignDeploySchedulerImpl implements CampaignDeployScheduler {
    private Vertx vertx;
    private CampaignRepository campaignRepository;
    private CampaignInstaller campaignInstaller;
    private ConcurrentMap<UUID, Long> installTask = new ConcurrentHashMap<>();
    private volatile long scanTask;

    @Inject
    public CampaignDeploySchedulerImpl(Vertx vertx, CampaignRepository campaignRepository, CampaignInstaller campaignInstaller) {
        this.vertx = vertx;
        this.campaignRepository = campaignRepository;
        this.campaignInstaller = campaignInstaller;
        setupScan();
    }

    private void setupScan() {
        scanTask = vertx.setPeriodic(60_000, t -> {
            Query<Campaign> query = null;// ("started_at")
            campaignRepository.findAll(query).map(campaigns -> {
                campaigns.forEach(this::schedule);
                return (Void) null;
            });
        });
    }

    @Override
    public Future<Void> schedule(Campaign campaign) {
        var now = OffsetDateTime.now();
        if (isRunning(campaign, now)) {
            return campaign.getActive() ? activate(campaign) : deactivate(campaign);
        }
        long delayMs = now.until(campaign.getStartedAt(), ChronoUnit.MILLIS);
        if (delayMs > 0 && delayMs < 3600000) {
            long taskId = vertx.setTimer(delayMs, t -> campaignInstaller.install(campaign));
            installTask.put(campaign.getId(), taskId);
        }
        return null;
    }

    @Override
    public Future<Void> activate(Campaign campaign) {
        if (isRunning(campaign)) {
            return campaignInstaller.install(campaign);
        }
        return Future.succeededFuture();
    }

    private boolean isRunning(Campaign campaign) {
        return isRunning(campaign, OffsetDateTime.now());
    }

    private boolean isRunning(Campaign campaign, OffsetDateTime now) {
        if (now.isBefore(campaign.getStartedAt())) {
            return false;
        } else if (campaign.getCompletedAt() == null) {
            return true;
        }
        return now.isBefore(campaign.getCompletedAt());
    }

    @Override
    public Future<Void> deactivate(Campaign campaign) {
        if (isRunning(campaign)) {
            return campaignInstaller.uninstall(campaign);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> remove(Campaign campaign) {
        return campaignInstaller.uninstall(campaign);
    }

    @Override
    public void shutdown() {
        vertx.cancelTimer(scanTask);
        installTask.forEach((campaignId, installId) -> vertx.cancelTimer(installId));
    }
}
