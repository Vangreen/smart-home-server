package com.smarthome.server.dao;

import com.smarthome.server.entity.SceneryConfigurationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SceneryRepository extends MongoRepository<SceneryConfigurationModel, Integer> {

    @Query("{roomID:?0}")
    List<SceneryConfigurationModel> findByRoomID(int roomID);

    @Query("{sceneryName:'?0' , roomID:?1}")
    Optional<SceneryConfigurationModel> findBySceneryNameAndId(String sceneryName, int roomID);

    @Query("{sceneryStatus:'?0', roomID:?1}")
    Optional<List<SceneryConfigurationModel>> findBySceneryStatusAndId(String sceneryStatus, int roomID);

    @Query("{sceneryStatus:'?0' , roomID:?1}")
    Optional<SceneryConfigurationModel> findBySceneryStatusAndRoomID(String sceneryStatus, int roomID);
}
