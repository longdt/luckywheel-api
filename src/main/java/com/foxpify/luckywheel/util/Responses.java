package com.foxpify.luckywheel.util;

import com.foxpify.shopifyapi.util.Json;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class Responses {
    public static void ok(RoutingContext routingContext, Object response) {
        json(routingContext, 200, response);
    }

    public static void ok(RoutingContext routingContext) {
        json(routingContext).end();
    }

    public static void redirect(RoutingContext routingContext, String locationUrl) {
        routingContext.response().putHeader("Location", locationUrl).setStatusCode(307).end();
    }

    public static void badRequest(RoutingContext routingContext, Object response) {
        json(routingContext, 400, response);
    }

    public static void notFound(RoutingContext routingContext, Object response) {
        json(routingContext, 404, response);
    }

    public static void internalServerError(RoutingContext routingContext, Object response) {
        json(routingContext, 500, response);
    }

    public static void json(RoutingContext routingContext, int httpStatus, Object response) {
        json(routingContext).setStatusCode(httpStatus).end(Json.encodeToBuffer(response));
    }

    public static HttpServerResponse json(RoutingContext routingContext) {
        return routingContext.response().putHeader("Content-type", "application/json");
    }
}
