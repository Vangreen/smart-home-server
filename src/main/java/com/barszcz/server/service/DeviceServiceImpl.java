package com.barszcz.server.service;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.DeviceConfigurationInSceneryDao;
import com.barszcz.server.dao.UnassignedDeviceDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.Requests.ChangeDeviceColorRequest;
import com.barszcz.server.entity.Requests.ChangeDeviceStatusRequest;
import com.barszcz.server.entity.Requests.RenameDeviceRequest;
import com.barszcz.server.entity.Responses.ColorChangeResponse;
import com.barszcz.server.entity.Responses.SimpleResponse;
import com.barszcz.server.entity.Responses.StatusChangeResponse;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.exception.ChangeColorException;
import com.barszcz.server.exception.ChangeDeviceStatusException;
import com.barszcz.server.scheduler.ScheduleDelayTask;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private DeviceConfigurationDao deviceConfigurationDao;
    private UnassignedDeviceDao unassignedDeviceDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduleDelayTask scheduleDelayTask;
    private SceneryService sceneryService;
    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;

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
        deviceConfigurationModel.setFloatingStatus("Off");
        deviceConfigurationModel.setFloatingSpeed(50);
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

    public void changeDeviceColor(int serial, ChangeDeviceColorRequest changeDeviceColorRequest) throws Exception {
        System.out.println("color change for device:" + serial);
        Hsv hsv = new Hsv(changeDeviceColorRequest.getHue(), changeDeviceColorRequest.getSaturation(), changeDeviceColorRequest.getBrightness());
        String status = changeDeviceColorRequest.getStatus();
        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            sceneryService.validateSceneryByDeviceStatus(serial, status, hsv, deviceConfigurationModel.getRoomID());
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationModel.setHue(hsv.getHue());
            deviceConfigurationModel.setSaturation(hsv.getSaturation());
            deviceConfigurationModel.setBrightness(hsv.getBright());
            deviceConfigurationModel.setFloatingStatus(changeDeviceColorRequest.getFloatingStatus());
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, new ColorChangeResponse(status, hsv.getHue(), hsv.getBright(), hsv.getSaturation(), changeDeviceColorRequest.getFloatingStatus(), deviceConfigurationModel.getFloatingSpeed()));
            return true;
        })
                .orElseThrow(() -> new ChangeColorException("Change device error"));
    }

    public void changeDeviceStatus(int serial, ChangeDeviceStatusRequest changeDeviceStatusRequest) throws Exception {
        System.out.println("state change for device:" + serial);
        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            String status = changeDeviceStatusRequest.getStatus();
            sceneryService.validateSceneryByDeviceStatus(serial, status, null, deviceConfigurationModel.getRoomID());
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationModel.setFloatingStatus(changeDeviceStatusRequest.getFloatingStatus());
            deviceConfigurationModel.setFloatingSpeed(changeDeviceStatusRequest.getFloatingSpeed());
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, new StatusChangeResponse(status, changeDeviceStatusRequest.getFloatingStatus(), changeDeviceStatusRequest.getFloatingSpeed()));
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
            return new SimpleResponse("doesnt exists");
        }
    }


    public void deleteDevice(int serial) {
        deviceConfigurationDao.deleteBySerialLike(serial);
        System.out.println("deleted device with serial:" + serial);
        deviceConfigurationInSceneryDao.deleteDeviceConfigurationInSceneryModelsByDeviceSerialLike(serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
    }


}
