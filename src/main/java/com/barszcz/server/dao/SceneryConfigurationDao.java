package com.barszcz.server.dao;

import com.barszcz.server.entity.SceneryConfigurationModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SceneryConfigurationDao extends CrudRepository<SceneryConfigurationModel, Long> {

    List<SceneryConfigurationModel> findSceneryConfigurationModelsByRoomIDLike(int roomId);
}
