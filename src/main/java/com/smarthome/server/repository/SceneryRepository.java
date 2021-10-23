package com.smarthome.server.repository;

import com.smarthome.server.entity.Scenery;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SceneryRepository extends MongoRepository<Scenery, Integer> {

}
