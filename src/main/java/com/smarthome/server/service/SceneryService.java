package com.smarthome.server.service;
import com.smarthome.server.entity.Hsv;
import com.smarthome.server.entity.SceneryConfigurationModel;
import com.smarthome.server.entity.SceneryCreation;


public interface SceneryService {

    void addScenery(SceneryCreation sceneryCreation);

    void changeSceneryStatus(int sceneryID, SceneryConfigurationModel sceneryConfigurationModel) throws Exception;

    void validateSceneryByDeviceStatus(int deviceSerial, String deviceStatus, Hsv hsv, int roomID);

    void deleteScenery(int sceneryID);
}
