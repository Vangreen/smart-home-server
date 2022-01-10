package com.smarthome.server.service;

import com.smarthome.server.entity.Device;
import com.smarthome.server.entity.requests.RenameDeviceRequest;
import com.smarthome.server.entity.responses.ColorChangeResponse;
import com.smarthome.server.entity.responses.SimpleResponse;
import com.smarthome.server.entity.responses.StatusChangeResponse;
import com.smarthome.server.entity.UnassignedDevice;
import com.smarthome.server.exception.ChangeColorException;
import com.smarthome.server.exception.ChangeDeviceStatusException;
import com.smarthome.server.repository.DeviceRepository;
import com.smarthome.server.repository.UnassignedDeviceRepository;
import com.smarthome.server.scheduler.RemoveUnassignedDevicesScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class DeviceServiceImpl implements DeviceService {

    private DeviceRepository deviceRepository;
    private UnassignedDeviceRepository unassignedDeviceRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RemoveUnassignedDevicesScheduler removeUnassignedDevicesScheduler;

    public List<UnassignedDevice> findAll() {
        return unassignedDeviceRepository.findAll();
    }


    public void changeStatus(int serial) {
        deviceRepository.findById(serial).ifPresent(device -> {
                    log.info("state change for device:" + serial);
                    saveAndSend(setStatus(device));
                }
        );
    }

    public void turnOffAllDevices() {
        log.info("Turn off all");
        deviceRepository.findAll()
                .stream()
                .peek(device -> device.setDeviceStatus("Off"))
                .forEach(this::saveAndSend);
    }

    public void turnOnAllDevices() {
        log.info("Turn on all");
        deviceRepository.findAll()
                .stream()
                .peek(device -> device.setDeviceStatus("On"))
                .forEach(this::saveAndSend);
    }

    public void addDevice(Device device) {
        int serial = device.getSerial();
//        device.setDeviceConnectionStatus("connected");
        device.setHue(0);
        device.setSaturation(0);
        device.setBrightness(100);
        device.setDeviceStatus("On");
        deviceRepository.save(device);
        unassignedDeviceRepository.deleteById(serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, device);
        log.info("added device with serial:" + serial);
    }

    public void renameDevice(RenameDeviceRequest renameDeviceRequest) {
        deviceRepository.findById(renameDeviceRequest.getDeviceSerial()).map(device -> {
            device.setDeviceName(renameDeviceRequest.getNewDeviceName());
            return deviceRepository.save(device);
        });
    }

    public void changeDeviceColor(int serial, Device device) throws Exception {
        log.info("color change for device:" + serial);
        String status = device.getDeviceStatus();
        deviceRepository.findById(serial).map(deviceConfigurationModel -> {
//                    sceneryService.validateSceneryByDeviceStatus(serial, status, hsv, deviceConfigurationModel.getRoomID());
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceConfigurationModel.setHue(device.getHue());
                    deviceConfigurationModel.setSaturation(device.getSaturation());
                    deviceConfigurationModel.setBrightness(device.getBrightness());
                    deviceRepository.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + serial, new ColorChangeResponse(status, device.getHue(), device.getBrightness(), device.getSaturation()));
                    return true;
                })
                .orElseThrow(() -> new ChangeColorException("Change device error"));
    }

    public void changeDeviceStatus(int serial, String status) throws Exception {
        log.info("state change for device:" + serial);
        deviceRepository.findBySerial(serial).map(device -> {
            setStatus(device);
            saveAndSend(device);
            return device;
        }).orElseThrow(() -> new ChangeDeviceStatusException("Change device error"));

    }


    public void updateDeviceStatus(int serial, String status) throws Exception {
        log.info("state change for device:" + serial);
        deviceRepository.findById(serial).map(deviceConfigurationModel -> {
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceRepository.save(deviceConfigurationModel);
                    return true;
                })
                .orElseThrow(() -> new ChangeDeviceStatusException("Change device error"));
    }


    public void createNewDevice(int serial, String deviceType) {
        UnassignedDevice unassignedDevice = new UnassignedDevice();
        unassignedDevice.setSerial(serial);
        unassignedDevice.setDeviceType(deviceType);
        unassignedDeviceRepository.save(unassignedDevice);
        simpMessagingTemplate.convertAndSend("/device/unassignedDevices", unassignedDeviceRepository.findAll());
        removeUnassignedDevicesScheduler.deleteUnassignedDevices(serial);
        log.info("new unassigned device with serial:" + serial);
    }

    public Object initDevice(int serial) {
        log.info("new device subscribed for serial:" + serial);
        if (deviceRepository.findById(serial).isPresent()) {
            return deviceRepository.findById(serial);
        } else {
            return new SimpleResponse("doesnt exists");
        }
    }


    public void deleteDevice(int serial) {
        deviceRepository.deleteBySerial(serial);
        log.info("deleted device with serial:" + serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
    }

    private void saveAndSend(Device device) {
        deviceRepository.save(device);
        simpMessagingTemplate.convertAndSend("/device/device/" + device.getSerial(), new StatusChangeResponse(device.getDeviceStatus()));
        log.info("Change by http device status:" + device.getSerial() + " to: " + device.getDeviceStatus());
    }

    /*
     *  TODO Temporary fix
     *  In device code is bug
     *  Remove when fixed
     */
    private Device setStatus(Device device) {
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
        return device;
    }
}
