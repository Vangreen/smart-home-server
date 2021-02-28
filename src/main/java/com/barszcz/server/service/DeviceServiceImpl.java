package com.barszcz.server.service;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.UnassignedDeviceDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.Requests.RenameDeviceRequest;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.exception.ChangeColorException;
import com.barszcz.server.exception.ChangeDeviceStatusException;
import com.barszcz.server.scheduler.ScheduleDelayTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private DeviceConfigurationDao deviceConfigurationDao;
    private UnassignedDeviceDao unassignedDeviceDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduleDelayTask scheduleDelayTask;

    @Autowired
    private ObjectMapper mapper;


    public List<UnassignedDeviceModel> findAll() {
        return (List<UnassignedDeviceModel>) unassignedDeviceDao.findAll();
    }

    public void addDevice(DeviceConfigurationModel device) {
        DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
        int serial = device.getSerial();
        deviceConfigurationModel.setSerial(serial);
        deviceConfigurationModel.setDeviceName(device.getDeviceName());
        deviceConfigurationModel.setRoomID(device.getRoomID());
        deviceConfigurationModel.setDeviceType(device.getDeviceType());
        deviceConfigurationModel.setDeviceConnectionStatus("connected");
        deviceConfigurationModel.setHue(0);
        deviceConfigurationModel.setSaturation(0);
        deviceConfigurationModel.setBrightness(100);
        deviceConfigurationModel.setDeviceStatus("On");
        deviceConfigurationDao.save(deviceConfigurationModel);
        unassignedDeviceDao.deleteBySerialLike(serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, deviceConfigurationModel);
        System.out.println("added device with serial:" + serial);
    }

    public void renameDevice(RenameDeviceRequest renameDeviceRequest){
        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(renameDeviceRequest.getDeviceSerial()).map(device->{
            device.setDeviceName(renameDeviceRequest.getNewDeviceName());
            return deviceConfigurationDao.save(device);
        });
    }

    public void changeDeviceColor(int serial, String status, Hsv hsv) throws Exception {
        System.out.println("color change for device:" + serial);

        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationModel.setHue(hsv.getHue());
            deviceConfigurationModel.setSaturation(hsv.getSaturation());
            deviceConfigurationModel.setBrightness(hsv.getBright());
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, colorChange(status, hsv.getHue(), hsv.getBright(), hsv.getSaturation()));
            return true;
        })
                .orElseThrow(() -> new ChangeColorException("Change device error"));
    }

    public void changeDeviceStatus(int serial, String status) throws Exception {
        System.out.println("state change for device:" + serial);
        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, statusChange(status));
            return true;
        })
                .orElseThrow(() -> new ChangeDeviceStatusException("Change device error"));
    }


    public void doesntExist(int serial, String deviceType) {
        UnassignedDeviceModel unassignedDeviceModel = new UnassignedDeviceModel();
        unassignedDeviceModel.setSerial(serial);
        unassignedDeviceModel.setDeviceType(deviceType);
        unassignedDeviceDao.save(unassignedDeviceModel);
        simpMessagingTemplate.convertAndSend("/device/unassignedDevices", unassignedDeviceDao.findAll());
        scheduleDelayTask.deleteUnassignedDevices(serial);
        System.out.println("new unassigned device with serial:" + serial);
    }

    public Object initDevice(int serial) {
        System.out.println("new device subscribed for serial:" + serial);
        if (deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).isPresent()) {
            return deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial);
        } else {
            return responseObject("doesnt exists");
        }
    }


    public void deleteDevice(int serial) {
        deviceConfigurationDao.deleteBySerialLike(serial);
        System.out.println("deleted device with serial:" + serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, responseObject("doesnt exists"));
    }


    private HashMap<String, String> statusChange(String status) {
        HashMap<String, String> map = new HashMap<>();
        map.put("task", "status change");
        map.put("status", status);
        return map;
    }

    private ObjectNode colorChange(String status, int hue, int bright, int sat) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("task", "color change");
        objectNode.put("status", status);
        objectNode.put("hue", hue);
        objectNode.put("brightness", bright);
        objectNode.put("saturation", sat);
        return objectNode;
    }

    private ObjectNode responseObject(String response) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("response", response);
        return objectNode;
    }
}
