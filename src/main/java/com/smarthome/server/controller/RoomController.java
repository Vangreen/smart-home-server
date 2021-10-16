package com.smarthome.server.controller;

import com.smarthome.server.dao.RoomRepository;
import com.smarthome.server.entity.RoomConfigurationModel;
import com.smarthome.server.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class RoomController {

    private RoomRepository roomRepository;
    private RoomService roomService;


    @SubscribeMapping("/rooms")
    public List<RoomConfigurationModel> findRooms() {
        return roomRepository.findAll();

    }

    @PostMapping(path = "/addRoom")
    public void addRoom(@RequestBody RoomConfigurationModel room) {
        roomService.addRoom(room);
    }

    @DeleteMapping(path = "/deleteRoom/{id}")
    public void deleteDevice(@PathVariable("id") int id) {
        roomRepository.deleteById(id);
        System.out.println("deleted room with id:" + id);
        roomService.deleteRoom(id);
    }

    @PostMapping(path = "/renameRoom")
    public void editNameRoom(@RequestBody RoomConfigurationModel room) throws Exception {
        roomService.editName(room);
    }
}
