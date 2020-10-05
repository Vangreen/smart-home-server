package com.barszcz.server.dao;

import com.barszcz.server.entity.DeviceTypeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceTypeDao extends CrudRepository<DeviceTypeModel, Long> {
}
