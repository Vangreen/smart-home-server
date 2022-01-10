package com.smarthome.server.service;

import com.smarthome.server.entity.Device;
import com.smarthome.server.entity.requests.RenameDeviceRequest;
import com.smarthome.server.entity.UnassignedDevice;

import java.util.List;

public interface DeviceService {

    List<UnassignedDevice> findAll();

    void addDevice(Device device);

    void renameDevice(RenameDeviceRequest renameDeviceRequest);

    void deleteDevice(int serial);

    void changeDeviceColor(int serial, Device device) throws Exception;

    void changeDeviceStatus(int serial, String status) throws Exception;

    void updateDeviceStatus(int serial, String status) throws Exception;

    void createNewDevice(int serial, String deviceType) throws Exception;

    void changeStatus(int serial);

    void turnOffAllDevices();

    void turnOnAllDevices();

    Object initDevice(int serial);


}
