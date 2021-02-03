package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceConfigurationModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceConfigurationDao extends CrudRepository<DeviceConfigurationModel, Long> {

    public Optional<DeviceConfigurationModel> findDeviceConfigurationModelBySerialLike(int serial);

    public List<DeviceConfigurationModel> findConfigurationModelByRoomLike(String room);
    @Transactional
    public void deleteBySerialLike(int serial);


}
