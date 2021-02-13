package com.barszcz.server.controller;

import com.barszcz.server.dao.RoomConfigurationDao;
import com.barszcz.server.entity.RoomConfigurationModel;
import com.barszcz.server.service.JsonObjectService;
import com.barszcz.server.service.RoomService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class RoomController {

    private final static String ROOM_NAME = "roomName";
    private final static String MAIN_VALUE = "main";
    private final static String ID_VALUE = "id";

    private RoomConfigurationDao roomConfigurationDao;
    private RoomService roomService;
    private JsonObjectService jsonService;


    @SubscribeMapping("/rooms")
    public List<RoomConfigurationModel> findRooms() {
        return (List<RoomConfigurationModel>) roomConfigurationDao.findAll();

    }

    @PostMapping(path = "/addRoom")
    public void addRoom(@RequestBody String body) throws Exception {
        JSONObject jsonObject = jsonService.parse(body);
        String roomName = jsonService.getString(jsonObject, ROOM_NAME);
        String main = jsonService.getString(jsonObject, MAIN_VALUE);
        roomService.addRoom(roomName, main);
    }

    @DeleteMapping(path = "/deleteRoom/{id}")
    public void deleteDevice(@PathVariable("id") int id) {
        roomConfigurationDao.deleteRoomConfigurationModelByIdLike(id);
        System.out.println("deleted room with id:" + id);
        roomService.deleteRoom(id);
    }

    @PostMapping(path = "/renameRoom")
    public void editNameRoom(@RequestBody String body) throws Exception {
        JSONObject jsonObject = jsonService.parse(body);
        int id = jsonService.getInt(jsonObject, ID_VALUE);
        String name = jsonService.getString(jsonObject, ROOM_NAME);
        roomService.editName(name, id);
    }
}
