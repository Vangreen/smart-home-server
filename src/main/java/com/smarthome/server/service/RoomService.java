package com.smarthome.server.service;

import org.json.JSONException;

public interface RoomService {

    void addRoom(String roomName, String main) throws JSONException;

    void deleteRoom(int roomID);

    void editName(String roomName, int id) throws Exception;
}
