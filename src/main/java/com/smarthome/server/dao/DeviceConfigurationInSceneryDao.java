package com.smarthome.server.dao;

import com.smarthome.server.entity.DeviceConfigurationInSceneryModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface DeviceConfigurationInSceneryDao extends JpaRepository<DeviceConfigurationInSceneryModel, Integer> {

    List<DeviceConfigurationInSceneryModel> findById(int sceneryID);


    Optional<DeviceConfigurationInSceneryModel> findByDeviceSerialAndId(int deviceSerial, int sceneryID);

    @Transactional
    void deleteAllBySceneryIDLike(int sceneryID);

    @Transactional
    void deleteDeviceConfigurationInSceneryModelsByDeviceSerialLike(int deviceSerial);
}
