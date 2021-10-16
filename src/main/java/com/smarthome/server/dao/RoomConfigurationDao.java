package com.smarthome.server.dao;

import com.smarthome.server.entity.RoomConfigurationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomConfigurationDao extends JpaRepository<RoomConfigurationModel, Integer> {

}
