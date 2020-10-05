package com.barszcz.server.controller;

import com.barszcz.server.dao.ConfigurationDao;
import com.barszcz.server.entity.ConfigurationModel;
import com.barszcz.server.entity.Respond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final ConfigurationDao configurationDao;

    @MessageMapping("/type")
    @SendTo("/topic/test")
    public Respond broadcastNews(String message) {
        Respond respond = new Respond();
        respond.setRespond(message);
        return respond;
    }

    @GetMapping(path = "/add")
    public List<ConfigurationModel> postTestString(@RequestParam String ip, String name, int red, int green, int blue, String state, String room, String type) {
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setIp(ip);
        configurationModel.setDeviceName(name);
        configurationModel.setRed(red);
        configurationModel.setGreen(green);
        configurationModel.setBlue(blue);
        configurationModel.setDeviceState(state);
        configurationModel.setRoom(room);
        configurationModel.setDeviceType(type);
        configurationDao.save(configurationModel);
        return (List<ConfigurationModel>) configurationDao.findAll();

    }
}
