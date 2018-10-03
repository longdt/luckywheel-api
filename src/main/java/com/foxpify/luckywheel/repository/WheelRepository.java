package com.foxpify.luckywheel.repository;

import com.foxpify.luckywheel.model.entity.Wheel;
import com.foxpify.vertxorm.repository.CrudRepository;

import java.util.UUID;

public interface WheelRepository extends CrudRepository<UUID, Wheel> {
}
