package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceConfigurationInSceneryModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceConfigurationInSceneryDao extends CrudRepository<DeviceConfigurationInSceneryModel, Long> {

    List<DeviceConfigurationInSceneryModel> findRoomConfigurationInSceneryModelsBySceneryIDLike(int sceneryID);

}
