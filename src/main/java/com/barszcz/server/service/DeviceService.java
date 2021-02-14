package com.barszcz.server.service;

import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.UnassignedDeviceModel;

import java.util.List;

public interface DeviceService {

    List<UnassignedDeviceModel> findAll();

    void addDevice(DeviceConfigurationModel deviceConfigurationModel);

    void deleteDevice(int serial);

    void changeDeviceColor(int serial, String status, Hsv hsv) throws Exception;

    void changeDeviceStatus(int serial, String status) throws Exception;

    void doesntExist(int serial, String deviceType) throws Exception;

    Object initDevice(int serial);

}
