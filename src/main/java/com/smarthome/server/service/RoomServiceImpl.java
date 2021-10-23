package com.smarthome.server.service;

import com.smarthome.server.entity.Device;
import com.smarthome.server.entity.responses.SimpleResponse;
import com.smarthome.server.entity.Room;
import com.smarthome.server.repository.DeviceRepository;
import com.smarthome.server.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Log4j2
public class RoomServiceImpl implements RoomService {

    private DeviceRepository deviceRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomRepository roomRepository;


    public void addRoom(Room room) {
        roomRepository.save(room);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());

    }

    public void deleteRoom(int roomID) {
        List<Device> devices;
        devices = deviceRepository.findByRoomID(roomID);
        devices.forEach(device -> {
            int serial = device.getSerial();
            deviceRepository.deleteBySerial(serial);
            log.info("deleted device with serial:" + serial);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
        });
        roomRepository.deleteById(roomID);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());
    }

    public void editName(Room room) throws Exception {
        roomRepository.findById(room.getId()).map(deviceConfigurationModel -> {
                    deviceConfigurationModel.setRoomName(room.getRoomName());
                    return roomRepository.save(deviceConfigurationModel);
                }
        ).orElseThrow(Exception::new);
        log.info("Rename room with id:" + room.getId());
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());
    }

}
