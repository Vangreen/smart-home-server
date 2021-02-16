package com.barszcz.server.dao;

import com.barszcz.server.entity.RoomConfigurationInSceneryModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomConfigurationInSceneryDao extends CrudRepository<RoomConfigurationInSceneryModel, Long> {

    List<RoomConfigurationInSceneryModel> findRoomConfigurationInSceneryModelsBySceneryIDLike(int sceneryID);

}
