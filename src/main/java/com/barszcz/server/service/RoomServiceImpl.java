package com.barszcz.server.service;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.RoomConfigurationDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Responses.SimpleResponse;
import com.barszcz.server.entity.RoomConfigurationModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private DeviceConfigurationDao deviceConfigurationDao;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomConfigurationDao roomConfigurationDao;


    public void addRoom(String roomName, String main) {
        RoomConfigurationModel roomConfigurationModel = new RoomConfigurationModel();
        roomConfigurationModel.setRoomName(roomName);
        roomConfigurationModel.setMain(main);
        roomConfigurationDao.save(roomConfigurationModel);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());

    }


    public void deleteRoom(int roomID) {
        List<DeviceConfigurationModel> devices;
        devices = deviceConfigurationDao.findDeviceConfigurationModelsByRoomIDLike(roomID);
        devices.forEach(device -> {
            int serial = device.getSerial();
            deviceConfigurationDao.deleteBySerialLike(serial);
            System.out.println("deleted device with serial:" + serial);
            simpMessagingTemplate.convertAndSend("/device/device/" + serial, new SimpleResponse("doesnt exists"));
        });
        roomConfigurationDao.deleteRoomConfigurationModelByIdLike(roomID);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());
    }


    public void editName(String roomName, int id) throws Exception {
        roomConfigurationDao.findRoomConfigurationModelByIdLike(id).map(deviceConfigurationModel -> {
                    deviceConfigurationModel.setRoomName(roomName);
                    return roomConfigurationDao.save(deviceConfigurationModel);
                }
        ).orElseThrow(
                Exception::new
        );
        System.out.println("Rename room with id:" + id);
        simpMessagingTemplate.convertAndSend("/rooms/rooms", roomConfigurationDao.findAll());
    }

}
