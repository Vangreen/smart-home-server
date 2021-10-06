package com.barszcz.server.service;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.DeviceConfigurationInSceneryDao;
import com.barszcz.server.dao.UnassignedDeviceDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.Requests.RenameDeviceRequest;
import com.barszcz.server.entity.Responses.ColorChangeResponse;
import com.barszcz.server.entity.Responses.SimpleResponse;
import com.barszcz.server.entity.Responses.StatusChangeResponse;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.exception.ChangeColorException;
import com.barszcz.server.exception.ChangeDeviceStatusException;
import com.barszcz.server.scheduler.ScheduleDelayTask;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class DeviceServiceImpl implements DeviceService {

    private DeviceConfigurationDao deviceConfigurationDao;
    private UnassignedDeviceDao unassignedDeviceDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduleDelayTask scheduleDelayTask;
    private SceneryService sceneryService;
    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;

    public List<UnassignedDeviceModel> findAll() {
        return unassignedDeviceDao.findAll();
    }


    public void changeStatus(int serial) {
        deviceConfigurationDao.findById(serial).ifPresent(device -> {
                    log.info("state change for device:" + serial);

                    /*
                     *  TODO Temporary fix
                     *  In device code is bug
                     *  Remove when fixed
                     */
                    if (device.getDeviceStatus().contains("\"")) {
                        log.info("FIXME");
                        if (device.getDeviceStatus().contains("Off")) {
                            device.setDeviceStatus("Off");
                        } else if (device.getDeviceStatus().contains("On")) {
                            device.setDeviceStatus("On");
                        }
                    } else if (device.getDeviceStatus().contains("Off")) {
                        device.setDeviceStatus("On");
                    } else if (device.getDeviceStatus().contains("On")) {
                        device.setDeviceStatus("Off");
                    }

                    saveAndSend(device);
                }
        );
    }

    public void turnOffAllDevices() {
        log.info("Turn off all");
        deviceConfigurationDao.findAll()
                .stream()
                .peek(device -> device.setDeviceStatus("Off"))
                .forEach(this::saveAndSend);
    }

    public void turnOnAllDevices() {
        log.info("Turn on all");
        deviceConfigurationDao.findAll()
                .stream()
                .peek(device -> device.setDeviceStatus("On"))
                .forEach(this::saveAndSend);
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
        unassignedDeviceDao.deleteById(serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, deviceConfigurationModel);
        log.info("added device with serial:" + serial);
    }

    public void renameDevice(RenameDeviceRequest renameDeviceRequest) {
        deviceConfigurationDao.findById(renameDeviceRequest.getDeviceSerial()).map(device -> {
            device.setDeviceName(renameDeviceRequest.getNewDeviceName());
            return deviceConfigurationDao.save(device);
        });
    }

    public void changeDeviceColor(int serial, String status, Hsv hsv) throws Exception {
        log.info("color change for device:" + serial);

        deviceConfigurationDao.findById(serial).map(deviceConfigurationModel -> {
                    sceneryService.validateSceneryByDeviceStatus(serial, status, hsv, deviceConfigurationModel.getRoomID());
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceConfigurationModel.setHue(hsv.getHue());
                    deviceConfigurationModel.setSaturation(hsv.getSaturation());
                    deviceConfigurationModel.setBrightness(hsv.getBright());
                    deviceConfigurationDao.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + serial, new ColorChangeResponse(status, hsv.getHue(), hsv.getBright(), hsv.getSaturation()));
                    return true;
                })
                .orElseThrow(() -> new ChangeColorException("Change device error"));
    }

    public void changeDeviceStatus(int serial, String status) throws Exception {
        log.info("state change for device:" + serial);
        deviceConfigurationDao.findById(serial).map(deviceConfigurationModel -> {
                    sceneryService.validateSceneryByDeviceStatus(serial, status, null, deviceConfigurationModel.getRoomID());
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceConfigurationDao.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + serial, new StatusChangeResponse(status));
                    return true;
                })
                .orElseThrow(() -> new ChangeDeviceStatusException("Change device error"));
    }


    public void updateDeviceStatus(int serial, String status) throws Exception {
        log.info("state change for device:" + serial);
        deviceConfigurationDao.findById(serial).map(deviceConfigurationModel -> {
                    sceneryService.validateSceneryByDeviceStatus(serial, status, null, deviceConfigurationModel.getRoomID());
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceConfigurationDao.save(deviceConfigurationModel);
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
        log.info("new unassigned device with serial:" + serial);
    }

    public Object initDevice(int serial) {
        log.info("new device subscribed for serial:" + serial);
        if (deviceConfigurationDao.findById(serial).isPresent()) {
            return deviceConfigurationDao.findById(serial);
        } else {
            return new SimpleResponse("doesnt exists");
        }
    }


    public void deleteDevice(int serial) {
        deviceConfigurationDao.deleteBySerial(serial);
        log.info("deleted device with serial:" + serial);
        deviceConfigurationInSceneryDao.deleteDeviceConfigurationInSceneryModelsByDeviceSerialLike(serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
    }

    private void saveAndSend(DeviceConfigurationModel device) {
        deviceConfigurationDao.save(device);
        simpMessagingTemplate.convertAndSend("/device/device/" + device.getSerial(), new StatusChangeResponse(device.getDeviceStatus()));
        log.info("Change by http device status:" + device.getSerial() + " to: " + device.getDeviceStatus());
    }
}
