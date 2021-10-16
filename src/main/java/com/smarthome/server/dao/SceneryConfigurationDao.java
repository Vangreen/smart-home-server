package com.smarthome.server.dao;

import com.smarthome.server.entity.SceneryConfigurationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SceneryConfigurationDao extends JpaRepository<SceneryConfigurationModel, Integer> {

    List<SceneryConfigurationModel> findByRoomID(int roomId);

    Optional<SceneryConfigurationModel> findBySceneryNameAndId(String sceneryName, int roomID);

    Optional<List<SceneryConfigurationModel>> findBySceneryStatusAndId(String sceneryStatus, int roomID);

    Optional<SceneryConfigurationModel> findBySceneryStatusAndRoomID(String sceneryStatus, int roomID);
}
