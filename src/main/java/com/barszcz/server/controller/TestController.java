package com.barszcz.server.controller;

import com.barszcz.server.dao.*;
import com.barszcz.server.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Any;
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
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {


    private DeviceConfigurationDao deviceConfigurationDao;
    //    private UserDao userDao;
//    private UserSettingsRespondDao userSettingsRespondDao;
//    private RoomsDao roomsDao;
//    private DeviceTypeDao deviceTypeDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ObjectMapper mapper;

    @GetMapping(path = "/getDevices")
    public List<DeviceConfigurationModel> getAllDevices() {
        return (List<DeviceConfigurationModel>) deviceConfigurationDao.findAll();
    }

//    @PostMapping(path = "/init")
//    public String getDeviceInit(@RequestBody String body){
//        return body;
////        return (DeviceConfigurationModel) deviceConfigurationDao.findAll();
//    }

    @MessageMapping("/initdevice/{serial}")
    public void addDevice(@DestinationVariable("serial") int serial, @Payload String payload) {
        System.out.println(serial);
        System.out.println(payload);
        simpMessagingTemplate.convertAndSend("/device/device/" + serial, payload);
//        }
    }

    @SubscribeMapping("/device/{serial}")
    public Object initDevice(@DestinationVariable("serial") int serial) {
        System.out.println(serial);
        if (deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial).isPresent()) {
//            simpMessagingTemplate.convertAndSend("/device/" + serial, deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial));
            return deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(serial);
        } else {
            DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
            deviceConfigurationModel.setSerial(serial);
            deviceConfigurationModel.setIp("http://192.168.2.166:9999/mywebsocket");
            deviceConfigurationModel.setDeviceName("test");
            deviceConfigurationModel.setHue(100);
            deviceConfigurationModel.setSaturation(50);
            deviceConfigurationModel.setBrightness(50);
            deviceConfigurationModel.setDeviceStatus("off");
            deviceConfigurationModel.setDeviceConnectionStatus("connected");
            deviceConfigurationModel.setRoom("pawla");
            deviceConfigurationModel.setDeviceType("ledrgb");
            deviceConfigurationDao.save(deviceConfigurationModel);
            return deviceConfigurationModel;
//            simpMessagingTemplate.convertAndSend("/device/" + serial, deviceConfigurationModel);
        }
    }

    @MessageMapping("/changeDeviceStatus/{serial}")
    public void changeDeviceStatus(@DestinationVariable("serial") int serial, @Payload String payload) throws Exception {
        System.out.println(serial);
        System.out.println(payload);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
        System.out.println(jsonObject.get("status"));
        String status = (String) jsonObject.get("status");

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
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }

        String status = (String) jsonObject.get("status");
        int hue = (int) jsonObject.get("hue");
        int sat = (int) jsonObject.get("saturation");
        int bright = (int) jsonObject.get("brightness");

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



//    @GetMapping(path = "/find")
//    public List<DeviceConfigurationModel> getAllDevices(){
//        return (List<DeviceConfigurationModel>) configurationDao.findAll();
//    }
//
//    @GetMapping(path = "/findByIp")
//    public Optional<DeviceConfigurationModel> getDeviceByIp(@RequestParam String ip){
//        return  configurationDao.findConfigurationModelByIpLike(ip);
//    }
//
//    @GetMapping(path = "/findByRoom")
//    public List<DeviceConfigurationModel> getDevicesFromRoom(@RequestParam String room){
//        return configurationDao.findConfigurationModelByRoomLike(room);
//    }
//
//    @PutMapping(path = "/changeLedState")
//    public void putLedState(@RequestParam String ip, int red, int green, int blue) throws Exception {
//         configurationDao.findConfigurationModelByIpLike(ip).map(deviceConfigurationModel ->{
//            deviceConfigurationModel.setRed(red);
//            deviceConfigurationModel.setGreen(green);
//            deviceConfigurationModel.setBlue(blue);
//            return configurationDao.save(deviceConfigurationModel);
//        }).orElseThrow(
//                Exception::new
//        );
//    }
//
//    @PutMapping(path = "/changeState")
//    public void putState(@RequestParam String ip, String state) throws Exception {
//        configurationDao.findConfigurationModelByIpLike(ip).map(deviceConfigurationModel ->{
//            deviceConfigurationModel.setDeviceState(state);
//            return configurationDao.save(deviceConfigurationModel);
//        }).orElseThrow(
//                Exception::new
//        );
//    }
//
//
//    @GetMapping(path = "/add")
//    public List<DeviceConfigurationModel> postTestString(@RequestParam String ip, String name, int red, int green, int blue, String state, String room, String type){
//        DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
//        deviceConfigurationModel.setIp(ip);
//        deviceConfigurationModel.setDeviceName(name);
//        deviceConfigurationModel.setRed(red);
//        deviceConfigurationModel.setGreen(green);
//        deviceConfigurationModel.setBlue(blue);
//        deviceConfigurationModel.setDeviceState(state);
//        deviceConfigurationModel.setRoom(room);
//        deviceConfigurationModel.setDeviceType(type);
//        configurationDao.save(deviceConfigurationModel);
//        return (List<DeviceConfigurationModel>) configurationDao.findAll();
//
//    }
//
//    @GetMapping(path = "/delete")
//    public List<DeviceConfigurationModel> delete(){
//        configurationDao.deleteAll();
//        return (List<DeviceConfigurationModel>) configurationDao.findAll();
//    }
//
//    @DeleteMapping(path = "/deleteDevice")
//    public void deleteByDeviceName(@RequestParam String name){
//        configurationDao.deleteByDeviceNameLike(name);
//    }
//
//    @GetMapping(path = "/login")
//    public Respond login(@RequestParam String login, String password){
//        Respond respond = new Respond();
//        UserModel userModel = userDao.findUserModelByLoginLike(login);
//        if(userModel!=null){
//        if(userModel.getPassword().equals(password)){
//            respond.setRespond("logged");
//            respond.setAdmin(userModel.getAdmin());
//            return respond;
//        }else{
//            respond.setRespond("wrong password");
//            return respond;
//        }
//        }else{
//            respond.setRespond("user doesnt exist");
//            return respond;
//        }
//    }
//
//    @GetMapping(path = "/register")
//    public Respond register(@RequestParam String login, String password){
//        Respond respond = new Respond();
//        if(userDao.findUserModelByLoginLike(login)==null){
//            UserModel userModel = new UserModel();
//            userModel.setLogin(login);
//            userModel.setPassword(password);
//            userModel.setAdmin(false);
//            userDao.save(userModel);
//            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
//            respond.setRespond("registered");
//            respond.setAdmin(false);
//            return respond;
//        }else{
//            respond.setRespond("user exists");
//            return respond;
//        }
//    }
//
//    @GetMapping(path = "/usersList")
//    public List<UserModel> listUsers(){
//        return (List<UserModel>) userDao.findAll();
//    }
//
//    @GetMapping(path = "/getOnlyUsers")
//    public List<UserSettingsRespondModel> listOnlyUsers(){return (List<UserSettingsRespondModel>) userSettingsRespondDao.findAll();}
//
//    @GetMapping(path = "/roomsList")
//    public List<RoomModel> listRooms(){
//        return (List<RoomModel>) roomsDao.findAll();
//    }
//
//    @GetMapping(path = "/addRoom")
//    public Respond addRoom(@RequestParam String roomName){
//        Respond respond = new Respond();
//        if (roomsDao.findRoomModelByRoomLike(roomName)==null){
//            RoomModel roomModel = new RoomModel();
//            roomModel.setRoom(roomName);
//            roomsDao.save(roomModel);
//            respond.setRespond("added");
//            return respond;
//        }else{
//            respond.setRespond("room exists");
//            return respond;
//        }
//    }
//    @DeleteMapping(path = "/deleteRoom")
//    public void deleteRoom(@RequestParam String roomName){
//        roomsDao.deleteRoomModelByRoomLike(roomName);
//    }
//
//    @GetMapping(path = "/findDeviceTypes")
//    public List<DeviceTypeModel> findDeviceTypes(){
//        return (List<DeviceTypeModel>) deviceTypeDao.findAll();
//    }
//
//    @GetMapping(path = "/addDeviceType")
//    public void addDeviceType(@RequestParam String deviceType){
//        DeviceTypeModel deviceTypeModel = new DeviceTypeModel();
//        deviceTypeModel.setDevice_type(deviceType);
//        deviceTypeDao.save(deviceTypeModel);
//    }
//
//    @MessageMapping("/deleteUser/{userName}")
//    public void deleteUser(@DestinationVariable("userName") String username){
//        userDao.deleteUserModelByLoginLike(username);
//        simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
//    }
//
//    @MessageMapping("/changeUser/{userName}/{admin}")
//    public void changeUserAdminState(@DestinationVariable("userName")String username, @DestinationVariable("admin") Boolean admin) throws Exception {
//        userSettingsRespondDao.findUserSettingsRespondModelByLoginLike(username).map(userSettingsRespondModel->{
//            userSettingsRespondModel.setAdmin(admin);
//            userSettingsRespondDao.save(userSettingsRespondModel);
//            //simpMessagingTemplate.convertAndSend("/user/changeUser/"+username, userSettingsRespondModel);
//            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
//            return true;
//        }).orElseThrow(
//                Exception::new
//        );
//    }
//
//    @MessageMapping("/type")
//    @SendTo("/topic/test")
//    public Respond broadcastNews(String message) throws Exception{
//        Respond respond = new Respond();
//        respond.setRespond(message);
//        return respond;
//    }
//
//    @MessageMapping("/addRoom")
//    @SendTo("/rooms/change")
//    public List<RoomModel> broadcastRooms(String message) throws Exception{
//        ObjectMapper objectMapper = new ObjectMapper();
//        RoomRespondModel roomRespondModel = objectMapper.readValue(message,RoomRespondModel.class);
//        if (roomsDao.findRoomModelByRoomLike(roomRespondModel.getRoom()).isEmpty()){
//            RoomModel roomModel = new RoomModel();
//            roomModel.setRoom(roomRespondModel.getRoom());
//            roomModel.setRoom_type(roomRespondModel.getRoom_type());
//            roomModel.setAdmin(false);
//            roomsDao.save(roomModel);
//            return (List<RoomModel>) roomsDao.findAll();
//        }else{
//            return (List<RoomModel>) roomsDao.findAll();
//        }
//    }
//
//    @MessageMapping("/change/{roomName}/{admin}")
//    @SendTo("/rooms/change")
//    public void changeRoomAdminState(@DestinationVariable("roomName") String roomName, @DestinationVariable("admin") Boolean admin) throws Exception{
//        roomsDao.findRoomModelByRoomLike(roomName).map(roomModel->{
//            roomModel.setAdmin(admin);
//             roomsDao.save(roomModel);
//            return roomsDao.findAll();
//        }).orElseThrow(
//                Exception::new
//        );
//    }
//
//    @MessageMapping("/delete/{roomName}")
//    public void deleteRoomViaApp(@DestinationVariable("roomName") String roomName){
//        roomsDao.deleteRoomModelByRoomLike(roomName);
//        simpMessagingTemplate.convertAndSend("/rooms/change",roomsDao.findAll());
//    }
//    @MessageMapping("/startup/{ip}")
//    public void startupDevice(@DestinationVariable("ip") String ip){
//        if(configurationDao.findConfigurationModelByIpLike(ip).isPresent()){
//            simpMessagingTemplate.convertAndSend("/device/"+ip,configurationDao.findConfigurationModelByIpLike(ip));
//        }else{
//            simpMessagingTemplate.convertAndSend("/device/newDevice",ip);
//        }
//    }
//
//    @MessageMapping("/addDevice/{ip}/{deviceName}/{room}/{deviceType}")
//    public void addDevice(@DestinationVariable("ip") String ip, @DestinationVariable("deviceName") String deviceName, @DestinationVariable("room") String room, @DestinationVariable("deviceType") String deviceType){
//        DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
//        deviceConfigurationModel.setIp(ip);
//        deviceConfigurationModel.setDeviceName(deviceName);
//        deviceConfigurationModel.setRed(50);
//        deviceConfigurationModel.setGreen(50);
//        deviceConfigurationModel.setBlue(255);
//        deviceConfigurationModel.setDeviceState("on");
//        deviceConfigurationModel.setRoom(room);
//        deviceConfigurationModel.setDeviceType(deviceType);
//        configurationDao.save(deviceConfigurationModel);
//        simpMessagingTemplate.convertAndSend("/room/"+ deviceConfigurationModel.getRoom(),configurationDao.findConfigurationModelByRoomLike(deviceConfigurationModel.getRoom()));
//        simpMessagingTemplate.convertAndSend("/device/"+ip, deviceConfigurationModel);
//    }
//
//    @MessageMapping("/deleteDevice/{deviceIp}")
//    public void deleteDeviceByIP(@DestinationVariable("deviceIp") String ip){
//        DeviceConfigurationModel deviceConfigurationModel;
//        deviceConfigurationModel = configurationDao.findConfigurationModelByIpLike(ip).get();
//        configurationDao.deleteByIpLike(ip);
//        simpMessagingTemplate.convertAndSend("/room/"+ deviceConfigurationModel.getRoom(), configurationDao.findConfigurationModelByRoomLike(deviceConfigurationModel.getRoom()));
//        simpMessagingTemplate.convertAndSend("/room/roomCountChanged", configurationDao.findAll());
//    }
//
//    @MessageMapping("/changeDeviceRoom/{ip}/{currentRoom}/{newRoom}")
//    public void changeDeviceRoom(@DestinationVariable("ip")String ip,@DestinationVariable("currentRoom")String currentRoom, @DestinationVariable("newRoom")String newRoom) throws Exception{
//        configurationDao.findConfigurationModelByIpLike(ip).map(deviceConfigurationModel ->{
//            deviceConfigurationModel.setRoom(newRoom);
//            configurationDao.save(deviceConfigurationModel);
//            simpMessagingTemplate.convertAndSend("/room/"+currentRoom,configurationDao.findConfigurationModelByRoomLike(currentRoom));
//            simpMessagingTemplate.convertAndSend("/room/"+newRoom,configurationDao.findConfigurationModelByRoomLike(newRoom));
//            return true;
//        }).orElseThrow(
//                Exception::new
//        );
//    }
//
//    @MessageMapping("/{ip}/{r}/{g}/{b}/{state}")
//    //@SendTo("/device/{ip}")
//    public void broadcastDevice(@DestinationVariable("ip") String ip, @DestinationVariable("r") int r,@DestinationVariable("g") int g,@DestinationVariable("b") int b,@DestinationVariable("state") String state) throws Exception{
//        configurationDao.findConfigurationModelByIpLike(ip).map(deviceConfigurationModel ->{
//            deviceConfigurationModel.setRed(r);
//            deviceConfigurationModel.setGreen(g);
//            deviceConfigurationModel.setBlue(b);
//            deviceConfigurationModel.setDeviceState(state);
//            configurationDao.save(deviceConfigurationModel);
//            simpMessagingTemplate.convertAndSend("/device/"+ip, deviceConfigurationModel);
//            simpMessagingTemplate.convertAndSend("/room/"+ deviceConfigurationModel.getRoom(),configurationDao.findConfigurationModelByRoomLike(deviceConfigurationModel.getRoom()));
//            return true;
//        }).orElseThrow(
//                Exception::new
//        );
//
//    }


}
