package com.smarthome.server.service;

import com.smarthome.server.dao.DeviceRepository;
import com.smarthome.server.dao.RoomRepository;
import com.smarthome.server.entity.DeviceConfigurationModel;
import com.smarthome.server.entity.Responses.SimpleResponse;
import com.smarthome.server.entity.RoomConfigurationModel;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private DeviceRepository deviceRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomRepository roomRepository;


    public void addRoom(RoomConfigurationModel room) {
        roomRepository.save(room);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());

    }


    public void deleteRoom(int roomID) {
        List<DeviceConfigurationModel> devices;
        devices = deviceRepository.findByRoomID(roomID);
        devices.forEach(device -> {
            int serial = device.getSerial();
            deviceRepository.deleteBySerial(serial);
            System.out.println("deleted device with serial:" + serial);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
        });
        roomRepository.deleteById(roomID);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());
    }


    public void editName(RoomConfigurationModel room) throws Exception {
        roomRepository.findById(room.getId()).map(deviceConfigurationModel -> {
                    deviceConfigurationModel.setRoomName(room.getRoomName());
                    return roomRepository.save(deviceConfigurationModel);
                }
        ).orElseThrow(
                Exception::new
        );
        System.out.println("Rename room with id:" + room.getId());
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomRepository.findAll());
    }

}
