package com.barszcz.server.dao;

import com.barszcz.server.entity.UnassignedDeviceModel;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UnassignedDeviceDao extends CrudRepository<UnassignedDeviceModel, Long> {

    @Transactional
    void deleteBySerialLike(int serial);

    @Transactional
    Optional<UnassignedDeviceModel> findAllBySerialLike(int serial);
}
