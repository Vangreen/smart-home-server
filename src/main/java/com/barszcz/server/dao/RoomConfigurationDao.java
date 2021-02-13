package com.barszcz.server.dao;

import com.barszcz.server.entity.RoomConfigurationModel;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface RoomConfigurationDao extends CrudRepository<RoomConfigurationModel, Long> {

    RoomConfigurationModel findRoomConfigurationModelByMainLike(String main);

    Optional<RoomConfigurationModel> findRoomConfigurationModelByIdLike(int id);

    List<RoomConfigurationModel> findRoomConfigurationModelsByMainLike(String main);

    @Transactional
    void deleteRoomConfigurationModelByIdLike(int id);

}
