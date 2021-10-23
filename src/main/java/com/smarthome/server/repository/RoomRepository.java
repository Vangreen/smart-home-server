package com.smarthome.server.repository;

import com.smarthome.server.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, Integer> {

}
