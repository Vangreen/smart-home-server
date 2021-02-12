package com.barszcz.server.controller;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.UnassignedDeviceDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.UnassignedDeviceModel;
import com.barszcz.server.scheduler.ScheduleDelayTask;
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

@Slf4j
@RestController
@AllArgsConstructor
public class DeviceController {

    private DeviceConfigurationDao deviceConfigurationDao;
    private UnassignedDeviceDao unassignedDeviceDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduleDelayTask scheduleDelayTask;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping(path = "/getDevices")
    public List<DeviceConfigurationModel> getAllDevices(@RequestParam int roomID) {
        return (List<DeviceConfigurationModel>) deviceConfigurationDao.findDeviceConfigurationModelsByRoomIDLike(roomID);
    }

    @SubscribeMapping("/unassignedDevices")
    public List<UnassignedDeviceModel> initDevice() {
        return (List<UnassignedDeviceModel>) unassignedDeviceDao.findAll();
    }

    @PostMapping(path = "/addDevice")
    public void addDevice(@RequestBody String body) throws JSONException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
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
    public void deleteDevice(@RequestBody String body) throws JSONException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
        int serial = (int) jsonObject.get("serial");
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
    public void doesntExists(@Payload String payload) throws JSONException {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
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
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
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
