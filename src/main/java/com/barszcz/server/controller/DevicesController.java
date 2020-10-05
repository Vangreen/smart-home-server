package com.barszcz.server.controller;

import com.barszcz.server.dao.ConfigurationDao;
import com.barszcz.server.dao.DeviceTypeDao;
import com.barszcz.server.entity.ConfigurationModel;
import com.barszcz.server.entity.DeviceTypeModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final ConfigurationDao configurationDao;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DeviceTypeDao deviceTypeDao;

    @GetMapping(path = "/find")
    public List<ConfigurationModel> getAllDevices() {
        return (List<ConfigurationModel>) configurationDao.findAll();
    }

    @GetMapping(path = "/findByIp")
    public Optional<ConfigurationModel> getDeviceByIp(@RequestParam String ip) {
        return configurationDao.findConfigurationModelByIpLike(ip);
    }

    @GetMapping(path = "/findByRoom")
    public List<ConfigurationModel> getDevicesFromRoom(@RequestParam String room) {
        return configurationDao.findConfigurationModelByRoomLike(room);
    }

    @PutMapping(path = "/changeLedState")
    public void putLedState(@RequestParam String ip, int red, int green, int blue) throws Exception {
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel -> {
            configurationModel.setRed(red);
            configurationModel.setGreen(green);
            configurationModel.setBlue(blue);
            return configurationDao.save(configurationModel);
        }).orElseThrow(
                Exception::new
        );
    }

    @PutMapping(path = "/changeState")
    public void putState(@RequestParam String ip, String state) throws Exception {
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel -> {
            configurationModel.setDeviceState(state);
            return configurationDao.save(configurationModel);
        }).orElseThrow(
                Exception::new
        );
    }

    @GetMapping(path = "/delete")
    public List<ConfigurationModel> delete() {
        configurationDao.deleteAll();
        return (List<ConfigurationModel>) configurationDao.findAll();
    }

    @DeleteMapping(path = "/deleteDevice")
    public void deleteByDeviceName(@RequestParam String name) {
        configurationDao.deleteByDeviceNameLike(name);
    }


    @MessageMapping("/startup/{ip}")
    public void startupDevice(@DestinationVariable("ip") String ip) {
        if (configurationDao.findConfigurationModelByIpLike(ip).isPresent()) {
            simpMessagingTemplate.convertAndSend("/device/" + ip, configurationDao.findConfigurationModelByIpLike(ip));
        } else {
            simpMessagingTemplate.convertAndSend("/device/newDevice", ip);
        }
    }

    @MessageMapping("/addDevice/{ip}/{deviceName}/{room}/{deviceType}")
    public void addDevice(@DestinationVariable("ip") String ip, @DestinationVariable("deviceName") String deviceName, @DestinationVariable("room") String room, @DestinationVariable("deviceType") String deviceType) {
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setIp(ip);
        configurationModel.setDeviceName(deviceName);
        configurationModel.setRed(50);
        configurationModel.setGreen(50);
        configurationModel.setBlue(255);
        configurationModel.setDeviceState("on");
        configurationModel.setRoom(room);
        configurationModel.setDeviceType(deviceType);
        configurationDao.save(configurationModel);
        simpMessagingTemplate.convertAndSend("/room/" + configurationModel.getRoom(), configurationDao.findConfigurationModelByRoomLike(configurationModel.getRoom()));
        simpMessagingTemplate.convertAndSend("/device/" + ip, configurationModel);
    }

    @MessageMapping("/deleteDevice/{deviceIp}")
    public void deleteDeviceByIP(@DestinationVariable("deviceIp") String ip) {
        ConfigurationModel configurationModel;
        configurationModel = configurationDao.findConfigurationModelByIpLike(ip).get();
        configurationDao.deleteByIpLike(ip);
        simpMessagingTemplate.convertAndSend("/room/" + configurationModel.getRoom(), configurationDao.findConfigurationModelByRoomLike(configurationModel.getRoom()));
        simpMessagingTemplate.convertAndSend("/room/roomCountChanged", configurationDao.findAll());
    }

    @MessageMapping("/changeDeviceRoom/{ip}/{currentRoom}/{newRoom}")
    public void changeDeviceRoom(@DestinationVariable("ip") String ip, @DestinationVariable("currentRoom") String currentRoom, @DestinationVariable("newRoom") String newRoom) throws Exception {
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel -> {
            configurationModel.setRoom(newRoom);
            configurationDao.save(configurationModel);
            simpMessagingTemplate.convertAndSend("/room/" + currentRoom, configurationDao.findConfigurationModelByRoomLike(currentRoom));
            simpMessagingTemplate.convertAndSend("/room/" + newRoom, configurationDao.findConfigurationModelByRoomLike(newRoom));
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

    @MessageMapping("/{ip}/{r}/{g}/{b}/{state}")
    //@SendTo("/device/{ip}")
    public void broadcastDevice(@DestinationVariable("ip") String ip, @DestinationVariable("r") int r, @DestinationVariable("g") int g, @DestinationVariable("b") int b, @DestinationVariable("state") String state) throws Exception {
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel -> {
            configurationModel.setRed(r);
            configurationModel.setGreen(g);
            configurationModel.setBlue(b);
            configurationModel.setDeviceState(state);
            configurationDao.save(configurationModel);
            simpMessagingTemplate.convertAndSend("/device/" + ip, configurationModel);
            simpMessagingTemplate.convertAndSend("/room/" + configurationModel.getRoom(), configurationDao.findConfigurationModelByRoomLike(configurationModel.getRoom()));
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

    @GetMapping(path = "/findDeviceTypes")
    public List<DeviceTypeModel> findDeviceTypes() {
        return (List<DeviceTypeModel>) deviceTypeDao.findAll();
    }

    @GetMapping(path = "/addDeviceType")
    public void addDeviceType(@RequestParam String deviceType) {
        DeviceTypeModel deviceTypeModel = new DeviceTypeModel();
        deviceTypeModel.setDevice_type(deviceType);
        deviceTypeDao.save(deviceTypeModel);
    }

}
