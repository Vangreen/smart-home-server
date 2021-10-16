package com.smarthome.server.dao;

import com.smarthome.server.entity.RoomConfigurationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<RoomConfigurationModel, Integer> {

}
