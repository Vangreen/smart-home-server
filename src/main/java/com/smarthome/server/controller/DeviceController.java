package com.smarthome.server.controller;

import com.smarthome.server.entity.Device;
import com.smarthome.server.entity.UnassignedDevice;
import com.smarthome.server.entity.requests.RenameDeviceRequest;
import com.smarthome.server.repository.DeviceRepository;
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

    /*
     *  REST part
     */

    @GetMapping(path = "/findAllDevices")
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @GetMapping(path = "/getDeviceByRoomID")
    public List<Device> getDeviceByRoomId(@RequestParam int roomID) {
        return deviceRepository.findByRoomID(roomID);
    }

    @PostMapping(path = "/addDevice")
    public void addDevice(@RequestBody Device device) {
        deviceService.addDevice(device);
    }

    @PostMapping(path = "/renameDevice")
    public void remameDevice(@RequestBody RenameDeviceRequest renameDeviceRequest) {
        deviceService.renameDevice(renameDeviceRequest);
    }

    @PostMapping(path = "/deleteDevice")
    public void deleteDevice(@RequestBody Device device) {
        deviceService.deleteDevice(device.getSerial());
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


    /*
     *  WebSocket part
     */

    @SubscribeMapping("/unassignedDevices")
    public List<UnassignedDevice> initDevice() {
        return deviceService.findAll();
    }

    @SubscribeMapping("/device/{serial}")
    public Object initDevice(@DestinationVariable("serial") int serial) {
        return deviceService.initDevice(serial);
    }

    @MessageMapping("/doesntExists")
    public void doesntExists(@Payload Device device) throws Exception {
        deviceService.createNewDevice(device.getSerial(), device.getDeviceType());
    }

    @MessageMapping("/updateDeviceStatus")
    public void updateDeviceStatus(@Payload Device device) throws Exception {
        deviceService.updateDeviceStatus(device.getSerial(), device.getDeviceType());
    }

    @MessageMapping("/changeDeviceStatus/{serial}")
    public void changeDeviceStatus(@DestinationVariable("serial") int serial, @RequestBody String status) throws Exception {
        deviceService.changeDeviceStatus(serial, status);
    }

    @MessageMapping("/changeDeviceColor/{serial}")
    public void changeDeviceColor(@DestinationVariable("serial") int serial, @Payload Device device) throws Exception {
        deviceService.changeDeviceColor(serial, device);
    }
}
