package com.smarthome.server.service;

import com.smarthome.server.entity.Room;

public interface RoomService {

    void addRoom(Room room);

    void deleteRoom(int roomID);

    void editName(Room room) throws Exception;
}
