package com.barszcz.server.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.barszcz.server.dao.*;
import com.barszcz.server.entity.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {


    private ConfigurationDao configurationDao;
    private UserDao userDao;
    private RoomsDao roomsDao;
    private DeviceTypeDao deviceTypeDao;
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping(path = "/find")
    public List<ConfigurationModel> getAllDevices(){
        return (List<ConfigurationModel>) configurationDao.findAll();
    }

    @GetMapping(path = "/findByIp")
    public Optional<ConfigurationModel> getDeviceByIp(@RequestParam String ip){
        return  configurationDao.findConfigurationModelByIpLike(ip);
    }

    @GetMapping(path = "/findByRoom")
    public List<ConfigurationModel> getDevicesFromRoom(@RequestParam String room){
        return configurationDao.findConfigurationModelByRoomLike(room);
    }

    @PutMapping(path = "/changeLedState")
    public void putLedState(@RequestParam String ip, int red, int green, int blue) throws Exception {
         configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel->{
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
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel->{
            configurationModel.setDeviceState(state);
            return configurationDao.save(configurationModel);
        }).orElseThrow(
                Exception::new
        );
    }


    @GetMapping(path = "/add")
    public List<ConfigurationModel> postTestString(@RequestParam String ip, String name, int red, int green, int blue, String state, String room, String type){
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setIp(ip);
        configurationModel.setDeviceName(name);
        configurationModel.setRed(red);
        configurationModel.setGreen(green);
        configurationModel.setBlue(blue);
        configurationModel.setDeviceState(state);
        configurationModel.setRoom(room);
        configurationModel.setDeviceType(type);
        configurationDao.save(configurationModel);
        return (List<ConfigurationModel>) configurationDao.findAll();

    }

    @GetMapping(path = "/delete")
    public List<ConfigurationModel> delete(){
        configurationDao.deleteAll();
        return (List<ConfigurationModel>) configurationDao.findAll();
    }

    @GetMapping(path = "/login")
    public Respond login(@RequestParam String login, String password){
        Respond respond = new Respond();
        UserModel userModel = userDao.findUserModelByLoginLike(login);
        if(userModel!=null){
        if(userModel.getPassword().equals(password)){
            respond.setRespond("logged");
            return respond;
        }else{
            respond.setRespond("wrong password");
            return respond;
        }
        }else{
            respond.setRespond("user doesnt exist");
            return respond;
        }
    }

    @GetMapping(path = "/register")
    public Respond register(@RequestParam String login, String password){
        Respond respond = new Respond();
        if(userDao.findUserModelByLoginLike(login)==null){
            UserModel userModel = new UserModel();
            userModel.setLogin(login);
            userModel.setPassword(password);
            userModel.setAdmin("notAdmin");
            userDao.save(userModel);
            respond.setRespond("registered");
            return respond;
        }else{
            respond.setRespond("user exists");
            return respond;
        }
    }

    @GetMapping(path = "/roomsList")
    public List<RoomModel> listRooms(){
        return (List<RoomModel>) roomsDao.findAll();
    }

    @GetMapping(path = "/addRoom")
    public Respond addRoom(@RequestParam String roomName){
        Respond respond = new Respond();
        if (roomsDao.findRoomModelByRoomLike(roomName)==null){
            RoomModel roomModel = new RoomModel();
            roomModel.setRoom(roomName);
            roomsDao.save(roomModel);
            respond.setRespond("added");
            return respond;
        }else{
            respond.setRespond("room exists");
            return respond;
        }
    }
    @DeleteMapping(path = "/deleteRoom")
    public void deleteRoom(@RequestParam String roomName){
        roomsDao.deleteRoomModelByRoomLike(roomName);
    }

    @GetMapping(path = "/findDeviceTypes")
    public List<DeviceTypeModel> findDeviceTypes(){
        return (List<DeviceTypeModel>) deviceTypeDao.findAll();
    }

    @GetMapping(path = "/addDeviceType")
    public void addDeviceType(@RequestParam String deviceType){
        DeviceTypeModel deviceTypeModel = new DeviceTypeModel();
        deviceTypeModel.setDevice_type(deviceType);
        deviceTypeDao.save(deviceTypeModel);
    }

    @MessageMapping("/type")
    @SendTo("/topic/test")
    public Respond broadcastNews(String message) throws Exception{
        Respond respond = new Respond();
        respond.setRespond(message);
        return respond;
    }




}
