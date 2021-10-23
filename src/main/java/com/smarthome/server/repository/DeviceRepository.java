package com.smarthome.server.repository;

import com.smarthome.server.entity.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends MongoRepository<Device, Integer> {


    @Query("{roomID:?0}")
    List<Device> findByRoomID(int room);

    @Query("{serial:?0}")
    Optional<Device> findBySerial(int serial);

    @Query(value="{serial:?0}", delete = true)
    void deleteBySerial(int serial);

}
