package com.smarthome.server.dao;

import com.smarthome.server.entity.DeviceConfigurationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends MongoRepository<DeviceConfigurationModel, Integer> {


    @Query("{roomID:?0}")
    List<DeviceConfigurationModel> findByRoomID(int room);

    @Query("{serial:?0}")
    Optional<DeviceConfigurationModel> findBySerial(int serial);

    @Query(value="{serial:?0}", delete = true)
    void deleteBySerial(int serial);

}
