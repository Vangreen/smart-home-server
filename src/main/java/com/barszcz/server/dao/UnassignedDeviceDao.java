package com.barszcz.server.dao;

import com.barszcz.server.entity.UnassignedDeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UnassignedDeviceDao extends JpaRepository<UnassignedDeviceModel, Integer> {
}
