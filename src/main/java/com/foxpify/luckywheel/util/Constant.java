package com.foxpify.luckywheel.util;

public class Constant {
    public static String UNINSTALLED_TOPIC = "app/uninstalled";
    public static String AUTH_ENDPOINT = "/auth";
    public static String INSTALL_ENDPOINT = "/install";
    public static String UNINSTALL_ENDPOINT = "/uninstall";

    public static String SUBSCRIBE_ENDPOINT = "/subscribers/campaigns/:campaignId";
    public static String GET_SUBSCRIBERS_ENDPOINT = "/subscribers";

    public static String ADMIN_SUBROUTE_ENDPOINT = "/admin";

    public static String CREAT_CAMPAIGN_ENDPOINT = "/campaigns";
    public static String GET_CAMPAIGNS_ENDPOINT = CREAT_CAMPAIGN_ENDPOINT;
    public static String GET_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId";
    public static String UPDATE_CAMPAIGN_ENDPOINT = GET_CAMPAIGN_ENDPOINT;
    public static String DELETE_CAMPAIGN_ENDPOINT = GET_CAMPAIGN_ENDPOINT;
    public static String ACTIVATE_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId/activate";
    public static String DEACTIVATE_CAMPAIGN_ENDPOINT = "/campaigns/:campaignId/deactivate";
}
