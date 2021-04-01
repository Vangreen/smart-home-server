package com.barszcz.server.controller;

import com.barszcz.server.dao.DeviceConfigurationInSceneryDao;
import com.barszcz.server.dao.SceneryConfigurationDao;
import com.barszcz.server.entity.SceneryConfigurationModel;
import com.barszcz.server.entity.SceneryCreation;
import com.barszcz.server.service.SceneryService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class SceneryController {


    private SceneryService sceneryService;
    private SceneryConfigurationDao sceneryConfigurationDao;
    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;

    @PostMapping(path = "/addScenery")
    public void addScenery(@RequestBody SceneryCreation sceneryCreation){
        sceneryService.addScenery(sceneryCreation);
    }

    @DeleteMapping(path = "/deleteScenery/{sceneryID}")
    public void deleteScenery(@PathVariable int sceneryID){
        sceneryService.deleteScenery(sceneryID);

    }

    @MessageMapping("/changeSceneryStatus/{sceneryID}")
    public void changeSceneryStatus(@DestinationVariable int sceneryID, @Payload SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        sceneryService.changeSceneryStatus(sceneryID, sceneryConfigurationModel);
    }

    @SubscribeMapping("/sceneriesList/{roomID}")
    public List<SceneryConfigurationModel> getSceneriesList(@DestinationVariable int roomID){
        return sceneryConfigurationDao.findByRoomID(roomID);
    }

}
