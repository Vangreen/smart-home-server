package com.barszcz.server.controller;

import com.barszcz.server.dao.ConfigurationDao;
import com.barszcz.server.entity.ConfigurationModel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Executable;
import java.util.Optional;


@RestController
@AllArgsConstructor
public class TestController {


    private ConfigurationDao configurationDao;


    @GetMapping(path = "/find")
    public String getAllDevices(){
        return configurationDao.findAll().toString();
    }

    @GetMapping(path = "/findByName")
    public String getDeviceByName(@RequestParam String ip){
        return configurationDao.findConfigurationModelByIpLike(ip). toString();
    }

    @PutMapping(path = "/changeLedState")
    public void putLedState(@RequestParam String ip, int red, int green, int blue) throws Exception {
         configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel->{
            configurationModel.setRed(red);
            configurationModel.setGreen(green);
            configurationModel.setBlue(blue);
            return configurationDao.save(configurationModel);
        }).orElseThrow(
                Exception::new
        );
    }

    @PutMapping(path = "/changeState")
    public void putState(@RequestParam String ip, String state) throws Exception {
        configurationDao.findConfigurationModelByIpLike(ip).map(configurationModel->{
            configurationModel.setDeviceState(state);
            return configurationDao.save(configurationModel);
        }).orElseThrow(
                Exception::new
        );
    }


    @GetMapping(path = "/add")
    public String postTestString(@RequestParam String ip, String name, int red, int green, int blue, String state){
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setIp(ip);
        configurationModel.setDeviceName(name);
        configurationModel.setRed(red);
        configurationModel.setGreen(green);
        configurationModel.setBlue(blue);
        configurationModel.setDeviceState(state);
        configurationDao.save(configurationModel);
        return  configurationDao.findAll().toString();

    }

}
