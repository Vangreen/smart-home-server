package com.barszcz.server.controller;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.entity.Requests.RenameDeviceRequest;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.service.DeviceService;
import com.barszcz.server.service.JsonObjectService;
import com.barszcz.server.service.SceneryService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class DeviceController {

    private final static String SERIAL_VALUE = "serial";
    private final static String STATUS_VALUE = "status";
    private static final String DEVICE_TYPE_VALUE = "deviceType";

    private DeviceConfigurationDao deviceConfigurationDao;
    private JsonObjectService jsonService;
    private DeviceService deviceService;
    private SceneryService sceneryService;

    @GetMapping(path = "/getDevices")
    public List<DeviceConfigurationModel> getAllDevices(@RequestParam int roomID) {
        return deviceConfigurationDao.findByRoomID(roomID);
    }

    @SubscribeMapping("/unassignedDevices")
    public List<UnassignedDeviceModel> initDevice() {
        return deviceService.findAll();
    }

    @PostMapping(path = "/addDevice")
    public void addDevice(@RequestBody String body) throws Exception {
        JSONObject jsonObject = jsonService.parse(body);
        deviceService.addDevice(jsonService.bodyToDevice(jsonObject));

    }

    @PostMapping(path = "/renameDevice")
    public void remameDevice(@RequestBody RenameDeviceRequest renameDeviceRequest) {
        deviceService.renameDevice(renameDeviceRequest);
    }

    @PostMapping(path = "/deleteDevice")
    public void deleteDevice(@RequestBody String body) throws Exception {
        JSONObject jsonObject = jsonService.parse(body);
        int serial = jsonService.getInt(jsonObject, "serial");
        deviceService.deleteDevice(serial);
    }

    @SubscribeMapping("/device/{serial}")
    public Object initDevice(@DestinationVariable("serial") int serial) {
        return deviceService.initDevice(serial);
    }

    @MessageMapping("/doesntExists")
    public void doesntExists(@Payload String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);
        int serial = jsonService.getInt(jsonObject, SERIAL_VALUE);
        String deviceType = jsonService.getString(jsonObject, DEVICE_TYPE_VALUE);
        deviceService.doesntExist(serial, deviceType);
    }

    @MessageMapping("/updateDeviceStatus")
    public void updateDeviceStatus(@Payload String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);
        int serial = jsonService.getInt(jsonObject, SERIAL_VALUE);
        String deviceType = jsonService.getString(jsonObject, DEVICE_TYPE_VALUE);
        deviceService.updateDeviceStatus(serial, deviceType);
    }

    @MessageMapping("/changeDeviceStatus/{serial}")
    public void changeDeviceStatus(@DestinationVariable("serial") int serial, @RequestBody String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);
        String status = jsonService.getString(jsonObject, STATUS_VALUE);
        deviceService.changeDeviceStatus(serial, status);
    }

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
    public void changeDeviceColor(@DestinationVariable("serial") int serial, @Payload String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);
        String status = jsonService.getString(jsonObject, STATUS_VALUE);
        Hsv hsv = jsonService.bodyToHsv(jsonObject);
//        sceneryService.validateScenery(serial, status);
        deviceService.changeDeviceColor(serial, status, hsv);
    }


}
