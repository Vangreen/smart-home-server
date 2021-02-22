package com.barszcz.server.dao;

import com.barszcz.server.entity.SceneryConfigurationModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SceneryConfigurationDao extends CrudRepository<SceneryConfigurationModel, Long> {

    List<SceneryConfigurationModel> findSceneryConfigurationModelsByRoomIDLike(int roomId);

    Optional<SceneryConfigurationModel> findSceneryConfigurationModelByIdLike(int id);

    Optional<SceneryConfigurationModel> findSceneryConfigurationModelBySceneryNameLikeAndRoomIDLike(String sceneryName, int roomID);
}
