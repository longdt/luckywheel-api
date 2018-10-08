package com.foxpify.luckywheel.repository;

import com.foxpify.luckywheel.model.entity.Campaign;
import com.foxpify.vertxorm.repository.CrudRepository;

import java.util.UUID;

public interface CampaignRepository extends CrudRepository<UUID, Campaign> {
}
