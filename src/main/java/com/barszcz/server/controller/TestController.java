package com.barszcz.server.controller;

import com.barszcz.server.dao.*;
import com.barszcz.server.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {


    private final ConfigurationDao configurationDao;
    private final UserDao userDao;
    private final UserSettingsRespondDao userSettingsRespondDao;
    private final RoomsDao roomsDao;
    private final DeviceTypeDao deviceTypeDao;
    private final SimpMessagingTemplate simpMessagingTemplate;

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


    @GetMapping(path = "/add")
    public List<ConfigurationModel> postTestString(@RequestParam String ip, String name, int red, int green, int blue, String state, String room, String type) {
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
    public List<ConfigurationModel> delete() {
        configurationDao.deleteAll();
        return (List<ConfigurationModel>) configurationDao.findAll();
    }

    @DeleteMapping(path = "/deleteDevice")
    public void deleteByDeviceName(@RequestParam String name) {
        configurationDao.deleteByDeviceNameLike(name);
    }

    @GetMapping(path = "/login")
    public Respond login(@RequestParam String login, String password) {
        Respond respond = new Respond();
        UserModel userModel = userDao.findUserModelByLoginLike(login);
        if (userModel != null) {
            if (userModel.getPassword().equals(password)) {
                respond.setRespond("logged");
                respond.setAdmin(userModel.getAdmin());
                return respond;
            } else {
                respond.setRespond("wrong password");
                return respond;
            }
        } else {
            respond.setRespond("user doesnt exist");
            return respond;
        }
    }

    @GetMapping(path = "/register")
    public Respond register(@RequestParam String login, String password) {
        Respond respond = new Respond();
        if (userDao.findUserModelByLoginLike(login) == null) {
            UserModel userModel = new UserModel();
            userModel.setLogin(login);
            userModel.setPassword(password);
            userModel.setAdmin(false);
            userDao.save(userModel);
            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
            respond.setRespond("registered");
            respond.setAdmin(false);
            return respond;
        } else {
            respond.setRespond("user exists");
            return respond;
        }
    }

    @GetMapping(path = "/usersList")
    public List<UserModel> listUsers() {
        return (List<UserModel>) userDao.findAll();
    }

    @GetMapping(path = "/getOnlyUsers")
    public List<UserSettingsRespondModel> listOnlyUsers() {
        return (List<UserSettingsRespondModel>) userSettingsRespondDao.findAll();
    }

    @GetMapping(path = "/roomsList")
    public List<RoomModel> listRooms() {
        return (List<RoomModel>) roomsDao.findAll();
    }

    @GetMapping(path = "/addRoom")
    public Respond addRoom(@RequestParam String roomName) {
        Respond respond = new Respond();
        if (roomsDao.findRoomModelByRoomLike(roomName) == null) {
            RoomModel roomModel = new RoomModel();
            roomModel.setRoom(roomName);
            roomsDao.save(roomModel);
            respond.setRespond("added");
            return respond;
        } else {
            respond.setRespond("room exists");
            return respond;
        }
    }

    @DeleteMapping(path = "/deleteRoom")
    public void deleteRoom(@RequestParam String roomName) {
        roomsDao.deleteRoomModelByRoomLike(roomName);
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

    @MessageMapping("/deleteUser/{userName}")
    public void deleteUser(@DestinationVariable("userName") String username) {
        userDao.deleteUserModelByLoginLike(username);
        simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
    }

    @MessageMapping("/changeUser/{userName}/{admin}")
    public void changeUserAdminState(@DestinationVariable("userName") String username, @DestinationVariable("admin") Boolean admin) throws Exception {
        userSettingsRespondDao.findUserSettingsRespondModelByLoginLike(username).map(userSettingsRespondModel -> {
            userSettingsRespondModel.setAdmin(admin);
            userSettingsRespondDao.save(userSettingsRespondModel);
            //simpMessagingTemplate.convertAndSend("/user/changeUser/"+username, userSettingsRespondModel);
            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

    @MessageMapping("/type")
    @SendTo("/topic/test")
    public Respond broadcastNews(String message) throws Exception {
        Respond respond = new Respond();
        respond.setRespond(message);
        return respond;
    }

    @MessageMapping("/addRoom")
    @SendTo("/rooms/change")
    public List<RoomModel> broadcastRooms(String message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RoomRespondModel roomRespondModel = objectMapper.readValue(message, RoomRespondModel.class);
        if (roomsDao.findRoomModelByRoomLike(roomRespondModel.getRoom()).isEmpty()) {
            RoomModel roomModel = new RoomModel();
            roomModel.setRoom(roomRespondModel.getRoom());
            roomModel.setRoom_type(roomRespondModel.getRoom_type());
            roomModel.setAdmin(false);
            roomsDao.save(roomModel);
            return (List<RoomModel>) roomsDao.findAll();
        } else {
            return (List<RoomModel>) roomsDao.findAll();
        }
    }

    @MessageMapping("/change/{roomName}/{admin}")
    @SendTo("/rooms/change")
    public void changeRoomAdminState(@DestinationVariable("roomName") String roomName, @DestinationVariable("admin") Boolean admin) throws Exception {
        roomsDao.findRoomModelByRoomLike(roomName).map(roomModel -> {
            roomModel.setAdmin(admin);
            roomsDao.save(roomModel);
            return roomsDao.findAll();
        }).orElseThrow(
                Exception::new
        );
    }

    @MessageMapping("/delete/{roomName}")
    public void deleteRoomViaApp(@DestinationVariable("roomName") String roomName) {
        roomsDao.deleteRoomModelByRoomLike(roomName);
        simpMessagingTemplate.convertAndSend("/rooms/change", roomsDao.findAll());
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


}
