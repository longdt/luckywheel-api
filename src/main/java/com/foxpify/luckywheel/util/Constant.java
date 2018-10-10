package com.foxpify.luckywheel.util;

public class Constant {
    public static final String SETUP_THEME_ENDPOINT = "/publish_themes";
    public static final String UNINSTALLED_TOPIC = "app/uninstalled";
    public static final String PUBLISH_THEMES_TOPIC = "themes/publish";
    public static final String AUTH_ENDPOINT = "/auth";
    public static final String INSTALL_ENDPOINT = "/install";
    public static final String UNINSTALL_ENDPOINT = "/uninstall";

    public static final String SUBSCRIBE_ENDPOINT = "/subscribers/campaigns/:campaignId";
    public static final String GET_SUBSCRIBERS_ENDPOINT = "/subscribers";

    public static final String ADMIN_SUBROUTE_ENDPOINT = "/admin";

    public static final String CREAT_CAMPAIGN_ENDPOINT = "/campaigns";
    public static final String GET_CAMPAIGNS_ENDPOINT = CREAT_CAMPAIGN_ENDPOINT;
    public static final String GET_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId";
    public static final String UPDATE_CAMPAIGN_ENDPOINT = GET_CAMPAIGN_ENDPOINT;
    public static final String DELETE_CAMPAIGN_ENDPOINT = GET_CAMPAIGN_ENDPOINT;
    public static final String ACTIVATE_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId/activate";
    public static final String DEACTIVATE_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId/deactivate";

    public static final String SUBSRIBER_UNIQUE_ERROR = "shop_id_campaign_id_email_uniq_idx";
}
