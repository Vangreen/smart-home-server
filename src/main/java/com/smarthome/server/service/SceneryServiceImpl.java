package com.smarthome.server.service;

import com.smarthome.server.dao.DeviceConfigurationDao;
import com.smarthome.server.dao.DeviceConfigurationInSceneryDao;
import com.smarthome.server.dao.SceneryConfigurationDao;
import com.smarthome.server.entity.DeviceConfigurationInSceneryModel;
import com.smarthome.server.entity.Hsv;
import com.smarthome.server.entity.Responses.ColorChangeResponse;
import com.smarthome.server.entity.Responses.StatusChangeResponse;
import com.smarthome.server.entity.SceneryConfigurationModel;
import com.smarthome.server.entity.SceneryCreation;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SceneryServiceImpl implements SceneryService {

    private SceneryConfigurationDao sceneryConfigurationDao;
    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;
    private DeviceConfigurationDao deviceConfigurationDao;
    private SimpMessagingTemplate simpMessagingTemplate;


    public void addScenery(SceneryCreation sceneryCreation) {
        if (!sceneryConfigurationDao.findBySceneryNameAndId(sceneryCreation.getSceneryName(), sceneryCreation.getRoomID()).isPresent()) {
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
            simpMessagingTemplate.convertAndSend("/scenery/sceneriesList/" + sceneryConfigurationModel.getRoomID(), sceneryConfigurationDao.findByRoomID(sceneryConfigurationModel.getRoomID()));
            System.out.println(deviceConfigurationInSceneryDao.findAll());
        }
    }

    public void deleteScenery(int sceneryID) {
        sceneryConfigurationDao.findById(sceneryID).ifPresent(scenery -> {
            sceneryConfigurationDao.delete(scenery);
            simpMessagingTemplate.convertAndSend("/scenery/sceneriesList/" + scenery.getRoomID(), sceneryConfigurationDao.findByRoomID(scenery.getRoomID()));
        });
        deviceConfigurationInSceneryDao.deleteAllBySceneryIDLike(sceneryID);
        System.out.println("Deleted scenery with id: " + sceneryID);
    }


    public void changeSceneryStatus(int sceneryID, SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        String status = sceneryConfigurationModel.getSceneryStatus();
        int sceneryRoomID = sceneryConfigurationModel.getRoomID();

        if (status.equals("On")) {
            sceneryConfigurationDao.findBySceneryStatusAndId(status, sceneryRoomID).map(sceneries -> {
                sceneries.forEach(scenery -> {
                    scenery.setSceneryStatus("Off");
                    sceneryConfigurationDao.save(scenery);
                    simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
                });
                return null;
            });
        }

        sceneryConfigurationDao.findById(sceneryID).map(scenery -> {
                    scenery.setSceneryStatus(status);
                    sceneryConfigurationDao.save(scenery);
                    return scenery.getRoomID();
                }
        ).orElseThrow(
                Exception::new
        );


        List<DeviceConfigurationInSceneryModel> devicesInScenery = deviceConfigurationInSceneryDao.findById(sceneryID);
        devicesInScenery.forEach(device -> {
            int deviceSerial = device.getDeviceSerial();
            int deviceHue = device.getHue();
            int deviceSat = device.getSaturation();
            int deviceBright = device.getBrightness();
            deviceConfigurationDao.findById(deviceSerial).map(deviceConfigurationModel -> {
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
        simpMessagingTemplate.convertAndSend("/scenery/scenery/" + sceneryID, sceneryConfigurationDao.findById(sceneryID));
    }

    public void validateSceneryByDeviceStatus(int deviceSerial, String deviceStatus, Hsv hsv, int roomID) {
        sceneryConfigurationDao.findBySceneryStatusAndRoomID("On", roomID).ifPresent(scenery -> {
            deviceConfigurationInSceneryDao.findByDeviceSerialAndId(deviceSerial, scenery.getId()).ifPresent(device -> {
                if (!device.getDeviceState().equals(deviceStatus) || hsv != null && (hsv.getHue() != device.getHue() || hsv.getSaturation() != device.getSaturation() || hsv.getBright() != device.getBrightness())) {
                    scenery.setSceneryStatus("Off");
                    sceneryConfigurationDao.save(scenery);
                    simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
                }
            });
        });
    }


}


