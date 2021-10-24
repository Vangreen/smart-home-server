package com.smarthome.server.controller;

import com.smarthome.server.repository.RoomRepository;
import com.smarthome.server.entity.Room;
import com.smarthome.server.service.RoomService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
@Log4j2
public class RoomController {

    private RoomRepository roomRepository;
    private RoomService roomService;


    @SubscribeMapping("/rooms")
    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/rooms")
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping(path = "/addRoom")
    public void addRoom(@RequestBody Room room) {
        roomService.addRoom(room);
    }

    @DeleteMapping(path = "/deleteRoom/{id}")
    public void deleteDevice(@PathVariable("id") int id) {
        roomRepository.deleteById(id);
        log.info("deleted room with id:" + id);
        roomService.deleteRoom(id);
    }

    @PostMapping(path = "/renameRoom")
    public void editNameRoom(@RequestBody Room room) throws Exception {
        roomService.editName(room);
    }
}
