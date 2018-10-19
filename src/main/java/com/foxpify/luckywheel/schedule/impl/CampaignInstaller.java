package com.foxpify.luckywheel.schedule.impl;

import com.fizzed.rocker.BindableRockerModel;
import com.fizzed.rocker.Rocker;
import com.foxpify.luckywheel.conf.AppConf;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.service.ShopService;
import com.foxpify.luckywheel.util.ObjectHolder;
import com.foxpify.shopifyapi.client.Session;
import com.foxpify.shopifyapi.client.ShopifyClient;
import com.foxpify.shopifyapi.model.ScriptTag;
import com.foxpify.vertxorm.util.Futures;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.templ.impl.VertxBufferOutput;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class CampaignInstaller {
    private static final String CAMPAIGN_TEMPLATE_JS = "conf/campaign.rocker.js";
    private static final String SCRIPT_TAG_EVENT = "onload";
    private final FileSystem fileSystem;
    private ShopService shopService;
    private ShopifyClient shopifyClient;
    private String jsBaseUrl;

    @Inject
    public CampaignInstaller(AppConf appConf, Vertx vertx, ShopService shopService, ShopifyClient shopifyClient) {
        jsBaseUrl = appConf.getHttpHost() + appConf.getContextPath() + "/static/";
        fileSystem = vertx.fileSystem();
        this.shopService = shopService;
        this.shopifyClient = shopifyClient;
    }

    public Future<Void> install(Campaign campaign) {
        BindableRockerModel model = Rocker.template(CAMPAIGN_TEMPLATE_JS);
        model.bind("campaign", campaign);
        VertxBufferOutput output = model.render(VertxBufferOutput.FACTORY);
        Future<Void> createScript = Futures.toFuture(fileSystem::writeFile, getCampaignJsPath(campaign.getId()), output.getBuffer());
        Future<ScriptTag> embedShopify =
                shopService.getShop(campaign.getShopId())
                .map(shop -> shopifyClient.newSession(shop.getShop(), shop.getAccessToken()))
                .compose(session -> session.createScriptTag(SCRIPT_TAG_EVENT, getCampaignJsUrl(campaign.getId())));
        return CompositeFuture.all(createScript, embedShopify)
                .map((Void) null);
    }

    public Future<Void> uninstall(Campaign campaign) {
        Future<Void> deleteScript = Futures.toFuture(fileSystem::delete, "www/" + campaign.getId() + ".js");
        ObjectHolder<Session> sessionHolder = new ObjectHolder<>();
        Future<Void>  unembedShopify = shopService.getShop(campaign.getShopId())
                .map(shop -> {
                    var session = shopifyClient.newSession(shop.getShop(), shop.getAccessToken());
                    sessionHolder.setValue(session);
                    return session;
                })
                .compose(session -> session.findScriptTags(new JsonObject().put("src", getCampaignJsUrl(campaign.getId()))))
                .compose(scriptTags -> {
                    if (scriptTags.isEmpty()) {
                        return Future.succeededFuture();
                    }
                    return sessionHolder.getValue().deleteScriptTag(scriptTags.get(0).getId());
                });
        return CompositeFuture.all(deleteScript, unembedShopify).map((Void) null);
    }

    private String getCampaignJsPath(UUID campaignId) {
        return "www/" + campaignId + ".js";
    }

    private Future<Boolean> isInstalled(Campaign campaign) {
        return Futures.toFuture(fileSystem::exists, getCampaignJsPath(campaign.getId()));
    }

    private String getCampaignJsUrl(UUID campaignId) {
        return jsBaseUrl + campaignId + ".js";
    }
}
