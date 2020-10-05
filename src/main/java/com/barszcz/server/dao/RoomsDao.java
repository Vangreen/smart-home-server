package com.barszcz.server.dao;

import com.barszcz.server.entity.RoomModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
public interface RoomsDao extends CrudRepository<RoomModel, Long> {
    Optional<RoomModel> findRoomModelByRoomLike(String roomName);

    @Transactional
    void deleteRoomModelByRoomLike(String roomName);
}
