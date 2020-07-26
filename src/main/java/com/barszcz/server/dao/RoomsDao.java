package com.barszcz.server.dao;

import com.barszcz.server.entity.RoomModel;
import com.barszcz.server.entity.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomsDao extends CrudRepository<RoomModel, Long> {
    public RoomModel findRoomModelByRoomLike(String roomName);
}
