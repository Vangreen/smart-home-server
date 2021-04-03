package com.barszcz.server.dao;

import com.barszcz.server.entity.RoomConfigurationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomConfigurationDao extends JpaRepository<RoomConfigurationModel, Integer> {

}
