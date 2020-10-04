package com.barszcz.server.dao;

import com.barszcz.server.entity.ConfigurationModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationDao extends CrudRepository<ConfigurationModel, Long> {

    public Optional<ConfigurationModel> findConfigurationModelByIpLike(String ip);
    public List<ConfigurationModel> findConfigurationModelByRoomLike(String room);
    @Transactional
    public void deleteByDeviceNameLike(String name);

    @Transactional void deleteByIpLike(String ip);

}
