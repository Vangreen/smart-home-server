package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceConfigurationModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface DeviceConfigurationDao extends CrudRepository<DeviceConfigurationModel, Long> {

    Optional<DeviceConfigurationModel> findDeviceConfigurationModelBySerialLike(int serial);

    @Transactional
    void deleteBySerialLike(int serial);


}
