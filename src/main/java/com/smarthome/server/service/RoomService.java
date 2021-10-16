package com.smarthome.server.service;

import com.smarthome.server.entity.RoomConfigurationModel;

public interface RoomService {

    void addRoom(RoomConfigurationModel room);

    void deleteRoom(int roomID);

    void editName(RoomConfigurationModel room) throws Exception;
}
