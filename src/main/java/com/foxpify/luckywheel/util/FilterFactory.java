package com.foxpify.luckywheel.util;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.luckywheel.model.entity.Subscriber;
import com.foxpify.vertxorm.repository.query.And;
import com.foxpify.vertxorm.repository.query.Query;
import com.foxpify.vertxorm.repository.query.QueryFactory;
import io.vertx.core.MultiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.foxpify.vertxorm.repository.query.QueryFactory.isNull;

public class FilterFactory {
    public static Query<Subscriber> filterSubscriber(MultiMap params) {
        List<Query<Subscriber>> subQueries = new ArrayList<>();
        subQueries.add(query("created_at", params.get("createdAtMin"), QueryFactory::greaterThanOrEqualTo));
        subQueries.add(query("created_at", params.get("createdAtMax"), QueryFactory::lessThanOrEqualTo));
        subQueries.add(query("email", params.get("email"), QueryFactory::equal));
        subQueries.add(query("full_name", params.get("fullName"), QueryFactory::equal));
        subQueries.add(query("campaign_id", params.get("campaignId"), QueryFactory::equal));
        subQueries.add(query("campaign_name", params.get("campaignName"), QueryFactory::equal));
        subQueries.add(query("id", params.get("id"), QueryFactory::equal));
        return buildQuery(subQueries);
    }

    private static <E> Query<E> buildQuery(List<Query<E>> subQueries) {
        subQueries = subQueries.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (subQueries.isEmpty()) {
            return null;
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
        return buildQuery(subQueries);
    }
}
