package com.smarthome.server.dao;

import com.smarthome.server.entity.UnassignedDeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UnassignedDeviceDao extends JpaRepository<UnassignedDeviceModel, Integer> {
}
