package com.barszcz.server.controller;

import com.barszcz.server.entity.Requests.SceneriesGetRequest;
import com.barszcz.server.entity.SceneryConfigurationModel;
import com.barszcz.server.entity.SceneryCreation;
import com.barszcz.server.service.SceneryService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class SceneryController {

//    private JsonObjectService jsonService;
//    private SceneryConfigurationDao sceneryConfigurationDao;
//    private DeviceConfigurationInSceneryDao deviceConfigurationInSceneryDao;

    private SceneryService sceneryService;

    @PostMapping(path = "/addScenery")
    public void addScenery(@RequestBody SceneryCreation sceneryCreation){
        sceneryService.addScenery(sceneryCreation);
    }

    @PostMapping(path = "/getSceneries")
    public List<SceneryConfigurationModel> getSceneries(@RequestBody SceneriesGetRequest sceneriesGetRequest){
        return sceneryService.getSceneries(sceneriesGetRequest);
    }

    @MessageMapping("/changeSceneryStatus/{sceneryID}")
    public void changeSceneryStatus(@DestinationVariable int sceneryID, @Payload SceneryConfigurationModel sceneryConfigurationModel) throws Exception {
        sceneryService.changeSceneryStatus(sceneryID, sceneryConfigurationModel);
    }

}
