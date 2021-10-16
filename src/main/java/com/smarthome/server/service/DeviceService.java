package com.smarthome.server.service;

import com.smarthome.server.entity.DeviceConfigurationModel;
import com.smarthome.server.entity.Requests.RenameDeviceRequest;
import com.smarthome.server.entity.UnassignedDeviceModel;

import java.util.List;

public interface DeviceService {

    List<UnassignedDeviceModel> findAll();

    void addDevice(DeviceConfigurationModel deviceConfigurationModel);

    void renameDevice(RenameDeviceRequest renameDeviceRequest);

    void deleteDevice(int serial);

    void changeDeviceColor(int serial, DeviceConfigurationModel device) throws Exception;

    void changeDeviceStatus(int serial, String status) throws Exception;

    void updateDeviceStatus(int serial, String status) throws Exception;

    void doesntExist(int serial, String deviceType) throws Exception;

    void changeStatus(int serial);

    void turnOffAllDevices();

    void turnOnAllDevices();

    Object initDevice(int serial);


}
