package com.smarthome.server.dao;

import com.smarthome.server.entity.DeviceConfigurationInSceneryModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceSceneryRepository extends MongoRepository<DeviceConfigurationInSceneryModel, Integer> {

    @Query("{sceneryID:?0}")
    List<DeviceConfigurationInSceneryModel> findById(int sceneryID);

    @Query("{deviceSerial:?0, sceneryID:?1}")
    Optional<DeviceConfigurationInSceneryModel> findByDeviceSerialAndId(int deviceSerial, int sceneryID);

    @Query(value="{sceneryID:?0}", delete = true)
    void deleteAllBySceneryIDLike(int sceneryID);

    @Query(value="{deviceSerial:?0}", delete = true)
    void deleteDeviceConfigurationInSceneryModelsByDeviceSerialLike(int deviceSerial);
}
