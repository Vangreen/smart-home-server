package com.smarthome.server.dao;

import com.smarthome.server.entity.UnassignedDeviceModel;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UnassignedDeviceRepository extends MongoRepository<UnassignedDeviceModel, Integer> {
}
