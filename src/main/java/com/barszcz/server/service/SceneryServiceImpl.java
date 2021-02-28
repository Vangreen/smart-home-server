package com.barszcz.server.service;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.dao.DeviceConfigurationInSceneryDao;
import com.barszcz.server.dao.SceneryConfigurationDao;
import com.barszcz.server.entity.*;
import com.barszcz.server.entity.Requests.SceneriesGetRequest;
import com.barszcz.server.entity.Responses.ColorChangeResponse;
import com.barszcz.server.entity.Responses.StatusChangeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class SceneryServiceImpl implements SceneryService {

    private SceneryConfigurationDao sceneryConfigurationDao;
    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;
    private DeviceConfigurationDao deviceConfigurationDao;
    private SimpMessagingTemplate simpMessagingTemplate;


    public void addScenery(SceneryCreation sceneryCreation) {
        if (sceneryConfigurationDao.findSceneryConfigurationModelBySceneryNameLikeAndRoomIDLike(sceneryCreation.getSceneryName(), sceneryCreation.getRoomID()).isEmpty()) {
            SceneryConfigurationModel sceneryConfigurationModel = new SceneryConfigurationModel();
            sceneryConfigurationModel.setSceneryName(sceneryCreation.getSceneryName());
            sceneryConfigurationModel.setRoomID(sceneryCreation.getRoomID());
            sceneryConfigurationModel.setLogo(sceneryCreation.getSceneryLogo());
            sceneryConfigurationModel.setSceneryStatus("On");
            int sceneryID = sceneryConfigurationDao.save(sceneryConfigurationModel).getId();

            sceneryCreation.getDevices().forEach(device -> {
                try {
                    DeviceConfigurationInSceneryModel deviceConfigurationInSceneryModel = new DeviceConfigurationInSceneryModel();
                    deviceConfigurationInSceneryModel.setSceneryID(sceneryID);
                    deviceConfigurationInSceneryModel.setDeviceSerial(device.getSerial());
                    deviceConfigurationInSceneryModel.setHue(device.getHue());
                    deviceConfigurationInSceneryModel.setSaturation(device.getSaturation());
                    deviceConfigurationInSceneryModel.setBrightness(device.getBrightness());
                    deviceConfigurationInSceneryModel.setDeviceState(device.getDeviceStatus());
                    deviceConfigurationInSceneryDao.save(deviceConfigurationInSceneryModel);
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            System.out.println(deviceConfigurationInSceneryDao.findAll());
        }
    }

    public List<SceneryConfigurationModel> getSceneries(SceneriesGetRequest sceneriesGetRequest) {
        return sceneryConfigurationDao.findSceneryConfigurationModelsByRoomIDLike(sceneriesGetRequest.getRoomID());
    }

    public void changeSceneryStatus(int sceneryID, SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        String status = sceneryConfigurationModel.getSceneryStatus();
        int sceneryRoomID = sceneryConfigurationModel.getRoomID();

        if (status.equals("On")) {
            sceneryConfigurationDao.findSceneryConfigurationModelsBySceneryStatusLikeAndRoomIDLike(status, sceneryRoomID).map(sceneries -> {
                sceneries.forEach(scenery -> {
                    scenery.setSceneryStatus("Off");
                    sceneryConfigurationDao.save(scenery);
                    simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
                });
                return null;
            });
        }

        sceneryConfigurationDao.findSceneryConfigurationModelByIdLike(sceneryID).map(scenery -> {
                    scenery.setSceneryStatus(status);
                    sceneryConfigurationDao.save(scenery);
                    return scenery.getRoomID();
                }
        ).orElseThrow(
                Exception::new
        );


        List<DeviceConfigurationInSceneryModel> devicesInScenery = deviceConfigurationInSceneryDao.findRoomConfigurationInSceneryModelsBySceneryIDLike(sceneryID);
        devicesInScenery.forEach(device -> {
            int deviceSerial = device.getDeviceSerial();
            int deviceHue = device.getHue();
            int deviceSat = device.getSaturation();
            int deviceBright = device.getBrightness();
            deviceConfigurationDao.findDeviceConfigurationModelBySerialLike(deviceSerial).map(deviceConfigurationModel -> {
                if (status.equals("On")) {
                    deviceConfigurationModel.setDeviceStatus(device.getDeviceState());
                    deviceConfigurationModel.setHue(deviceHue);
                    deviceConfigurationModel.setSaturation(deviceSat);
                    deviceConfigurationModel.setBrightness(deviceBright);
                    deviceConfigurationDao.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + deviceSerial, new ColorChangeResponse(device.getDeviceState(), deviceHue, deviceBright, deviceSat));
                } else {
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceConfigurationDao.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + deviceSerial, new StatusChangeResponse(status));
                }
                return true;
            });


        });
        simpMessagingTemplate.convertAndSend("/scenery/scenery/" + sceneryID, sceneryConfigurationDao.findSceneryConfigurationModelByIdLike(sceneryID));
    }

    public void validateSceneryByDeviceStatus(int deviceSerial, String deviceStatus, Hsv hsv, int roomID) {
        sceneryConfigurationDao.findSceneryConfigurationModelBySceneryStatusLikeAndRoomIDLike("On", roomID).ifPresent(scenery -> {
            deviceConfigurationInSceneryDao.findDeviceConfigurationInSceneryModelByDeviceSerialLikeAndSceneryID(deviceSerial, scenery.getId()).ifPresent(device -> {
                if (!device.getDeviceState().equals(deviceStatus) || hsv != null && (hsv.getHue() != device.getHue() || hsv.getSaturation() != device.getSaturation() || hsv.getBright() != device.getBrightness())) {
                    scenery.setSceneryStatus("Off");
                    sceneryConfigurationDao.save(scenery);
                    simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
                }
            });
        });
    }


}


