package com.barszcz.server.controller;

import com.barszcz.server.dao.RoomConfigurationDao;
import com.barszcz.server.entity.RoomConfigurationModel;
import com.barszcz.server.exception.JsonObjectException;
import com.barszcz.server.parser.JsonObjectParser;
import com.barszcz.server.service.RoomService;
import org.json.JSONObject;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class RoomController {

    private final static String ROOM_NAME = "roomName";
    private final static String MAIN_VALUE = "main";
    private final static String ID_VALUE = "id";

    private RoomConfigurationDao roomConfigurationDao;
    private RoomService roomService;

    public RoomController(RoomConfigurationDao roomConfigurationDao, RoomService roomService) {
        this.roomConfigurationDao = roomConfigurationDao;
        this.roomService = roomService;
    }

    private JsonObjectParser jsonParser = new JsonObjectParser();

    @SubscribeMapping("/rooms")
    public List<RoomConfigurationModel> findRooms() {
        return (List<RoomConfigurationModel>) roomConfigurationDao.findAll();

    }

    @PostMapping(path = "/addRoom")
    public void addRoom(@RequestBody String body) throws Exception {
        JSONObject jsonObject = jsonParser.parse(body);
        String roomName = getString(jsonObject, ROOM_NAME);
        String main = getString(jsonObject, MAIN_VALUE);
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
        JSONObject jsonObject = jsonParser.parse(body);
        int id = getInt(jsonObject, ID_VALUE);
        String name = getString(jsonObject, ROOM_NAME);
        roomService.editName(name, id);
    }


    private String getString(JSONObject jsonObject, String value) throws JsonObjectException {
        try {
            return jsonObject.getString(value);
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }
    }

    private int getInt(JSONObject jsonObject, String value) throws JsonObjectException {
        try {
            return jsonObject.getInt(value);
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }
    }

}
