package com.barszcz.server.controller;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.UnassignedDeviceDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.exception.JsonObjectException;
import com.barszcz.server.exception.ParseException;
import com.barszcz.server.parser.JsonObjectParser;
import com.barszcz.server.scheduler.ScheduleDelayTask;
import com.barszcz.server.service.JsonObjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class DeviceController {

    private DeviceConfigurationDao deviceConfigurationDao;
    private UnassignedDeviceDao unassignedDeviceDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduleDelayTask scheduleDelayTask;
    private JsonObjectService jsonService;


    @Autowired
    private ObjectMapper mapper;

    @GetMapping(path = "/getDevices")
    public List<DeviceConfigurationModel> getAllDevices(@RequestParam int roomID) {
        return deviceConfigurationDao.findDeviceConfigurationModelsByRoomIDLike(roomID);
    }

    @SubscribeMapping("/unassignedDevices")
    public List<UnassignedDeviceModel> initDevice() {
        return (List<UnassignedDeviceModel>) unassignedDeviceDao.findAll();
    }

    @PostMapping(path = "/addDevice")
    public void addDevice(@RequestBody String body) throws JSONException, ParseException {
        JSONObject jsonObject = jsonService.parse(body);
        int serial = (int) jsonObject.get("serial");
        DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
        deviceConfigurationModel.setSerial(serial);
        deviceConfigurationModel.setDeviceName((String) jsonObject.get("deviceName"));
        deviceConfigurationModel.setRoomID((int) jsonObject.get("roomID"));
        deviceConfigurationModel.setDeviceType((String) jsonObject.get("deviceType"));
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

    @PostMapping(path = "/deleteDevice")
    public void deleteDevice(@RequestBody String body) throws JSONException, ParseException, JsonObjectException {
        JSONObject jsonObject = jsonService.parse(body);
        int serial =  jsonService.getInt(jsonObject, "serial");
        deviceConfigurationDao.deleteBySerialLike(serial);
        System.out.println("deleted device with serial:" + serial);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, responseObject("doesnt exists"));
    }

    @SubscribeMapping("/device/{serial}")
    public Object initDevice(@DestinationVariable("serial") int serial) {
        System.out.println("new device subscribed for serial:" + serial);
        if (deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).isPresent()) {
            return deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial);
        } else {
            return responseObject("doesnt exists");
        }
    }

    @MessageMapping("/doesntExists")
    public void doesntExists(@Payload String payload) throws JSONException, ParseException {

        JSONObject jsonObject = jsonService.parse(payload);
        int serial = (int) jsonObject.get("serial");
        String deviceType = (String) jsonObject.get("deviceType");
        UnassignedDeviceModel unassignedDeviceModel = new UnassignedDeviceModel();
        unassignedDeviceModel.setSerial(serial);
        unassignedDeviceModel.setDeviceType(deviceType);
        unassignedDeviceDao.save(unassignedDeviceModel);
        simpMessagingTemplate.convertAndSend("/device/unassignedDevices", unassignedDeviceDao.findAll());
        scheduleDelayTask.deleteUnassignedDevices(serial);
        System.out.println("new unassigned device with serial:" + serial);
    }

    @MessageMapping("/changeDeviceStatus/{serial}")
    public void changeDeviceStatus(@DestinationVariable("serial") int serial, @Payload String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);
        String status = (String) jsonObject.get("status");
        System.out.println("state change for device:" + serial);
        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, statusChange(status));
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

    @MessageMapping("/changeDeviceColor/{serial}")
    public void changeDeviceColor(@DestinationVariable("serial") int serial, @Payload String payload) throws Exception {
        JSONObject jsonObject = jsonService.parse(payload);

        String status = (String) jsonObject.get("status");
        int hue = (int) jsonObject.get("hue");
        int sat = (int) jsonObject.get("saturation");
        int bright = (int) jsonObject.get("brightness");
        System.out.println("color change for device:" + serial);

        deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).map(deviceConfigurationModel -> {
            deviceConfigurationModel.setDeviceStatus(status);
            deviceConfigurationModel.setHue(hue);
            deviceConfigurationModel.setSaturation(sat);
            deviceConfigurationModel.setBrightness(bright);
            deviceConfigurationDao.save(deviceConfigurationModel);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, colorChange(status, hue, bright, sat));
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

    public HashMap<String, String> statusChange(String status) {
        HashMap<String, String> map = new HashMap<>();
        map.put("task", "status change");
        map.put("status", status);
        return map;
    }

    public ObjectNode colorChange(String status, int hue, int bright, int sat) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("task", "color change");
        objectNode.put("status", status);
        objectNode.put("hue", hue);
        objectNode.put("brightness", bright);
        objectNode.put("saturation", sat);
        return objectNode;
    }

    public ObjectNode responseObject(String response) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("response", response);
        return objectNode;
    }
}
