package com.smarthome.server.controller;

import com.smarthome.server.dao.DeviceRepository;
import com.smarthome.server.entity.DeviceConfigurationModel;
import com.smarthome.server.entity.Requests.RenameDeviceRequest;
import com.smarthome.server.entity.UnassignedDeviceModel;
import com.smarthome.server.service.DeviceService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;

    @GetMapping(path = "/getDevices")
    public List<DeviceConfigurationModel> getAllDevices(@RequestParam int roomID) {
        return deviceRepository.findByRoomID(roomID);
    }

    @SubscribeMapping("/unassignedDevices")
    public List<UnassignedDeviceModel> initDevice() {
        return deviceService.findAll();
    }

    @PostMapping(path = "/addDevice")
    public void addDevice(@RequestBody DeviceConfigurationModel device) {
        deviceService.addDevice(device);

    }

    @PostMapping(path = "/renameDevice")
    public void remameDevice(@RequestBody RenameDeviceRequest renameDeviceRequest) {
        deviceService.renameDevice(renameDeviceRequest);
    }

    @PostMapping(path = "/deleteDevice")
    public void deleteDevice(@RequestBody DeviceConfigurationModel device) {
        deviceService.deleteDevice(device.getSerial());
    }

    @SubscribeMapping("/device/{serial}")
    public Object initDevice(@DestinationVariable("serial") int serial) {
        return deviceService.initDevice(serial);
    }

    @MessageMapping("/doesntExists")
    public void doesntExists(@Payload DeviceConfigurationModel device) throws Exception {
        deviceService.doesntExist(device.getSerial(), device.getDeviceType());
    }

    @MessageMapping("/updateDeviceStatus")
    public void updateDeviceStatus(@Payload DeviceConfigurationModel device) throws Exception {
        deviceService.updateDeviceStatus(device.getSerial(), device.getDeviceType());
    }

    @MessageMapping("/changeDeviceStatus/{serial}")
    public void changeDeviceStatus(@DestinationVariable("serial") int serial, @RequestBody String status) throws Exception {
        deviceService.changeDeviceStatus(serial, status);
    }

    //Get mapping because of ios shortcuts app TODO fix in future
    @GetMapping("/changeDeviceStatus-http/{serial}")
    public void changeDeviceStatusHttp(@PathVariable("serial") int serial) {
        deviceService.changeStatus(serial);
    }

    @GetMapping("/turn-off-all")
    public void turnOffAll() {
        deviceService.turnOffAllDevices();
    }

    @GetMapping("/turn-on-all")
    public void turnOnAll() {
        deviceService.turnOnAllDevices();
    }

    @MessageMapping("/changeDeviceColor/{serial}")
    public void changeDeviceColor(@DestinationVariable("serial") int serial, @Payload DeviceConfigurationModel device) throws Exception {
        deviceService.changeDeviceColor(serial, device);
    }


}
