package com.barszcz.server.controller;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.RoomConfigurationDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.RoomConfigurationModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class RoomController {

    private DeviceConfigurationDao deviceConfigurationDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomConfigurationDao roomConfigurationDao;
    @Autowired
    private ObjectMapper mapper;

    @SubscribeMapping("/rooms")
    public List<RoomConfigurationModel> findRooms() {
        return (List<RoomConfigurationModel>) roomConfigurationDao.findAll();
    }

    @PostMapping(path = "/addRoom")
    public void addRoom(@RequestBody String body) throws JSONException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
        assert jsonObject != null;
        String roomName = (String) jsonObject.get("roomName");
        String main = (String) jsonObject.get("main");
        RoomConfigurationModel roomConfigurationModel = new RoomConfigurationModel();
        roomConfigurationModel.setRoomName(roomName);
        roomConfigurationModel.setMain(main);
        roomConfigurationDao.save(roomConfigurationModel);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());
    }

    @DeleteMapping(path = "/deleteRoom/{id}")
    public void deleteDevice(@PathVariable("id") int id) {
        roomConfigurationDao.deleteRoomConfigurationModelByIdLike(id);
        System.out.println("deleted room with id:" + id);
        deleteDevicesFromRoom(id);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());
    }

    @PostMapping(path = "/renameRoom")
    public void editNameRoom(@RequestBody String body) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException err) {
            System.out.println(err.toString());
        }
        int id = (int) jsonObject.get("id");
        String name = (String) jsonObject.get("name");
        roomConfigurationDao.findRoomConfigurationModelByIdLike(id).map(deviceConfigurationModel -> {
                    deviceConfigurationModel.setRoomName(name);
                    return roomConfigurationDao.save(deviceConfigurationModel);
                }
        ).orElseThrow(
                Exception::new
        );
        System.out.println("Rename room with id:" + id);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());
    }

    private void deleteDevicesFromRoom(int roomID){
        List<DeviceConfigurationModel> devices = new ArrayList<DeviceConfigurationModel>();
        devices = deviceConfigurationDao.findDeviceConfigurationModelsByRoomIDLike(roomID);
        devices.forEach(device ->{
            int serial = device.getSerial();
            deviceConfigurationDao.deleteBySerialLike(serial);
            System.out.println("deleted device with serial:" + serial);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, responseObject("doesnt exists"));
        });
    }

    private ObjectNode roomResponse() {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("main", roomConfigurationDao.findRoomConfigurationModelByMainLike("yes").toString());
        objectNode.put("rest", roomConfigurationDao.findRoomConfigurationModelsByMainLike("no").toString());
        return objectNode;
    }

    public ObjectNode responseObject(String response) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("response", response);
        return objectNode;
    }


}
