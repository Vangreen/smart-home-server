package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceConfigurationInSceneryModel;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface DeviceConfigurationInSceneryDao extends CrudRepository<DeviceConfigurationInSceneryModel, Long> {

    List<DeviceConfigurationInSceneryModel> findRoomConfigurationInSceneryModelsBySceneryIDLike(int sceneryID);

    Optional<DeviceConfigurationInSceneryModel> findDeviceConfigurationInSceneryModelByDeviceSerialLikeAndSceneryID(int deviceSerial, int sceneryID);


    @Transactional
    void deleteAllBySceneryIDLike(int sceneryID);

    @Transactional
    void deleteDeviceConfigurationInSceneryModelsByDeviceSerialLike(int deviceSerial);
}
