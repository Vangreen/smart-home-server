package com.smarthome.server.controller;

import com.smarthome.server.dao.SceneryRepository;
import com.smarthome.server.entity.SceneryConfigurationModel;
import com.smarthome.server.entity.SceneryCreation;
import com.smarthome.server.service.SceneryService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class SceneryController {


    private SceneryService sceneryService;
    private SceneryRepository sceneryRepository;

    @PostMapping(path = "/addScenery")
    public void addScenery(@RequestBody SceneryCreation sceneryCreation) {
        sceneryService.addScenery(sceneryCreation);
    }

    @DeleteMapping(path = "/deleteScenery/{sceneryID}")
    public void deleteScenery(@PathVariable int sceneryID) {
        sceneryService.deleteScenery(sceneryID);

    }

    @MessageMapping("/changeSceneryStatus/{sceneryID}")
    public void changeSceneryStatus(@DestinationVariable int sceneryID, @Payload SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        sceneryService.changeSceneryStatus(sceneryID, sceneryConfigurationModel);
    }

    @SubscribeMapping("/sceneriesList/{roomID}")
    public List<SceneryConfigurationModel> getSceneriesList(@DestinationVariable int roomID) {
        return sceneryRepository.findByRoomID(roomID);
    }

}
