package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceConfigurationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DeviceConfigurationDao extends JpaRepository<DeviceConfigurationModel, Integer> {


    List<DeviceConfigurationModel> findByRoomID(int room);


    @Transactional
    void deleteBySerial(int serial);

}
