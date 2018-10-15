package com.foxpify.luckywheel.util;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.vertxorm.repository.query.*;
import io.vertx.core.MultiMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.foxpify.vertxorm.repository.query.QueryFactory.*;

public class FilterFactory {
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    public static Query<Subscriber> filterSubscriber(MultiMap params) {
        List<Query<Subscriber>> subQueries = new ArrayList<>();
        subQueries.add(query("created_at", params.get("createdAtMin"), QueryFactory::greaterThanOrEqualTo));
        subQueries.add(query("created_at", params.get("createdAtMax"), QueryFactory::lessThanOrEqualTo));
        subQueries.add(query("email", params.get("email"), QueryFactory::equal));
        subQueries.add(query("full_name", params.get("fullName"), QueryFactory::equal));
        subQueries.add(query("campaign_id", params.get("campaignId"), QueryFactory::equal));
        subQueries.add(query("campaign_name", params.get("campaignName"), QueryFactory::equal));
        subQueries.add(query("id", params.get("id"), QueryFactory::equal));
        List<Order<Subscriber>> orders = parseOrderBy(params.get("orderBy"), descending("created_at"));
        return buildQuery(subQueries).orderBy(orders);
    }

    @SafeVarargs
    public static <E> List<Order<E>> parseOrderBy(String orderBy, Order<E>... defaultOrders) {
        if (orderBy == null) {
            return Arrays.asList(defaultOrders);
        }
        String[] directions = orderBy.split(";");
        return Stream.of(directions).flatMap(direction -> {
            String[] order = direction.split(":");
            if (order.length != 2) {
                throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Invalid orderBy param");
            }
            String[] fieldNames = order[0].split(",");
            if (ASC.equals(order[1])) {
                return Stream.of(fieldNames).map(QueryFactory::<E>ascending);
            }
            return Stream.of(fieldNames).map(QueryFactory::<E>descending);
        }).collect(Collectors.toList());
    }

    private static <E> Query<E> buildQuery(List<Query<E>> subQueries) {
        subQueries = subQueries.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (subQueries.isEmpty()) {
            return all();
        } else if (subQueries.size() == 1) {
            return subQueries.get(0);
        } else {
            return new And<>(subQueries);
        }
    }

    private static <E> Query<E> query(String dbField, String value, BiFunction<String, String, Query<E>> operator) {
        if (value == null) {
            return null;
        } else if (value.isEmpty()) {
            return isNull(dbField);
        } else {
            return operator.apply(dbField, value);
        }
    }

    public static Query<Campaign> filterCampaign(MultiMap params) {
        List<Query<Campaign>> subQueries = new ArrayList<>();
        subQueries.add(query("created_at", params.get("createdAtMin"), QueryFactory::greaterThanOrEqualTo));
        subQueries.add(query("created_at", params.get("createdAtMax"), QueryFactory::lessThanOrEqualTo));

        subQueries.add(query("started_at", params.get("startedAtMin"), QueryFactory::greaterThanOrEqualTo));
        subQueries.add(query("started_at", params.get("startedAtMax"), QueryFactory::lessThanOrEqualTo));

        subQueries.add(query("completed_at", params.get("completedAtMin"), QueryFactory::greaterThanOrEqualTo));
        subQueries.add(query("completed_at", params.get("completedAtMax"), QueryFactory::lessThanOrEqualTo));

        subQueries.add(query("active", params.get("active"), QueryFactory::equal));
        subQueries.add(query("name", params.get("name"), QueryFactory::equal));
        subQueries.add(query("id", params.get("id"), QueryFactory::equal));
        List<Order<Campaign>> orders = parseOrderBy(params.get("orderBy"), descending("updated_at"));
        return buildQuery(subQueries).orderBy(orders);
    }
}
