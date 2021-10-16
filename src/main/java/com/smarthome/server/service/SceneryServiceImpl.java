package com.smarthome.server.service;

import com.smarthome.server.dao.DeviceRepository;
import com.smarthome.server.dao.DeviceSceneryRepository;
import com.smarthome.server.dao.SceneryRepository;
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

    private SceneryRepository sceneryRepository;
    private DeviceSceneryRepository deviceSceneryRepository;
    private DeviceRepository deviceRepository;
    private SimpMessagingTemplate simpMessagingTemplate;


    public void addScenery(SceneryCreation sceneryCreation) {
        if (sceneryRepository.findBySceneryNameAndId(sceneryCreation.getSceneryName(), sceneryCreation.getRoomID()).isEmpty()) {
            SceneryConfigurationModel sceneryConfigurationModel = new SceneryConfigurationModel();
            sceneryConfigurationModel.setSceneryName(sceneryCreation.getSceneryName());
            sceneryConfigurationModel.setRoomID(sceneryCreation.getRoomID());
            sceneryConfigurationModel.setLogo(sceneryCreation.getSceneryLogo());
            sceneryConfigurationModel.setSceneryStatus("On");
            int sceneryID = sceneryRepository.save(sceneryConfigurationModel).getId();

            sceneryCreation.getDevices().forEach(device -> {
                try {
                    DeviceConfigurationInSceneryModel deviceConfigurationInSceneryModel = new DeviceConfigurationInSceneryModel();
                    deviceConfigurationInSceneryModel.setSceneryID(sceneryID);
                    deviceConfigurationInSceneryModel.setDeviceSerial(device.getSerial());
                    deviceConfigurationInSceneryModel.setHue(device.getHue());
                    deviceConfigurationInSceneryModel.setSaturation(device.getSaturation());
                    deviceConfigurationInSceneryModel.setBrightness(device.getBrightness());
                    deviceConfigurationInSceneryModel.setStatus(device.getDeviceStatus());
                    deviceSceneryRepository.save(deviceConfigurationInSceneryModel);
                } catch (Exception e) {
                    throw new SecurityException(e);
                }
            });
            simpMessagingTemplate.convertAndSend("/scenery/sceneriesList/" + sceneryConfigurationModel.getRoomID(), sceneryRepository.findByRoomID(sceneryConfigurationModel.getRoomID()));
            System.out.println(deviceSceneryRepository.findAll());
        }
    }

    public void deleteScenery(int sceneryID) {
        sceneryRepository.findById(sceneryID).ifPresent(scenery -> {
            sceneryRepository.delete(scenery);
            simpMessagingTemplate.convertAndSend("/scenery/sceneriesList/" + scenery.getRoomID(), sceneryRepository.findByRoomID(scenery.getRoomID()));
        });
        deviceSceneryRepository.deleteAllBySceneryIDLike(sceneryID);
        System.out.println("Deleted scenery with id: " + sceneryID);
    }


    public void changeSceneryStatus(int sceneryID, SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        String status = sceneryConfigurationModel.getSceneryStatus();
        int sceneryRoomID = sceneryConfigurationModel.getRoomID();

        if (status.equals("On")) {
            sceneryRepository.findBySceneryStatusAndId(status, sceneryRoomID).map(sceneries -> {
                sceneries.forEach(scenery -> {
                    scenery.setSceneryStatus("Off");
                    sceneryRepository.save(scenery);
                    simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
                });
                return null;
            });
        }

        sceneryRepository.findById(sceneryID).map(scenery -> {
                    scenery.setSceneryStatus(status);
                    sceneryRepository.save(scenery);
                    return scenery.getRoomID();
                }
        ).orElseThrow(
                Exception::new
        );


        List<DeviceConfigurationInSceneryModel> devicesInScenery = deviceSceneryRepository.findById(sceneryID);
        devicesInScenery.forEach(device -> {
            int deviceSerial = device.getDeviceSerial();
            int deviceHue = device.getHue();
            int deviceSat = device.getSaturation();
            int deviceBright = device.getBrightness();
            deviceRepository.findById(deviceSerial).map(deviceConfigurationModel -> {
                if (status.equals("On")) {
                    deviceConfigurationModel.setDeviceStatus(device.getStatus());
                    deviceConfigurationModel.setHue(deviceHue);
                    deviceConfigurationModel.setSaturation(deviceSat);
                    deviceConfigurationModel.setBrightness(deviceBright);
                    deviceRepository.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + deviceSerial, new ColorChangeResponse(device.getStatus(), deviceHue, deviceBright, deviceSat));
                } else {
                    deviceConfigurationModel.setDeviceStatus(status);
                    deviceRepository.save(deviceConfigurationModel);
                    simpMessagingTemplate.convertAndSend("/device/device/" + deviceSerial, new StatusChangeResponse(status));
                }
                return true;
            });


        });
        simpMessagingTemplate.convertAndSend("/scenery/scenery/" + sceneryID, sceneryRepository.findById(sceneryID));
    }

    public void validateSceneryByDeviceStatus(int deviceSerial, String deviceStatus, Hsv hsv, int roomID) {
        sceneryRepository.findBySceneryStatusAndRoomID("On", roomID).ifPresent(scenery -> deviceSceneryRepository.findByDeviceSerialAndId(deviceSerial, scenery.getId()).ifPresent(device -> {
            if (!device.getStatus().equals(deviceStatus) || hsv != null && (hsv.getHue() != device.getHue() || hsv.getSaturation() != device.getSaturation() || hsv.getBright() != device.getBrightness())) {
                scenery.setSceneryStatus("Off");
                sceneryRepository.save(scenery);
                simpMessagingTemplate.convertAndSend("/scenery/scenery/" + scenery.getId(), scenery);
            }
        }));
    }


}


