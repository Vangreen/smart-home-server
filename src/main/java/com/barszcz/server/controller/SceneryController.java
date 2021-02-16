package com.barszcz.server.controller;

import com.barszcz.server.dao.RoomConfigurationInSceneryDao;
import com.barszcz.server.dao.SceneryConfigurationDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.service.JsonObjectService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class SceneryController {

    private JsonObjectService jsonService;
    private SceneryConfigurationDao sceneryConfigurationDao;
    private RoomConfigurationInSceneryDao roomConfigurationInSceneryDao;

    @PostMapping(path = "/addScenery")
    public void addScenery(@RequestBody List<DeviceConfigurationModel> devices){
        System.out.println(devices);
    }

}
