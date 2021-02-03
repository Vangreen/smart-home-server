package com.barszcz.server.dao;

import com.barszcz.server.entity.UnassignedDeviceModel;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface UnassignedDeviceDao extends CrudRepository<UnassignedDeviceModel, Long> {

    @Transactional
    public void deleteBySerialLike(int serial);
}
