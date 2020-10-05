package com.barszcz.server.controller;

import com.barszcz.server.dao.RoomsDao;
import com.barszcz.server.entity.Respond;
import com.barszcz.server.entity.RoomModel;
import com.barszcz.server.entity.RoomRespondModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomsDao roomsDao;
    private final SimpMessagingTemplate simpMessagingTemplate;

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
}