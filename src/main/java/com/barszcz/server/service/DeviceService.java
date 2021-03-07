package com.barszcz.server.service;

import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.Requests.ChangeDeviceColorRequest;
import com.barszcz.server.entity.Requests.ChangeDeviceStatusRequest;
import com.barszcz.server.entity.Requests.RenameDeviceRequest;
import com.barszcz.server.entity.UnassignedDeviceModel;

import java.util.List;

public interface DeviceService {

    List<UnassignedDeviceModel> findAll();

    void addDevice(DeviceConfigurationModel deviceConfigurationModel);

    void renameDevice(RenameDeviceRequest renameDeviceRequest);

    void deleteDevice(int serial);

    void changeDeviceColor(int serial, ChangeDeviceColorRequest changeDeviceColorRequest) throws Exception;

    void changeDeviceStatus(int serial, ChangeDeviceStatusRequest changeDeviceStatusRequest) throws Exception;

    void doesntExist(int serial, String deviceType) throws Exception;

    Object initDevice(int serial);


}
