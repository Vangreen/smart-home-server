package com.barszcz.server;

import com.barszcz.server.dao.ConfigurationDao;
import com.barszcz.server.entity.ConfigurationModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ConfigurationDao dao){
        return(args) ->{
            ConfigurationModel configurationModel = new ConfigurationModel();
            configurationModel.setIp("192.168.1.1");
            configurationModel.setDeviceName("test");
            configurationModel.setRed(111);
            configurationModel.setGreen(222);
            configurationModel.setBlue(333);
            configurationModel.setDeviceState("state");
            configurationModel.setRoom("pawla");
            configurationModel.setDeviceType("ledRGB");
            dao.save(configurationModel);
        };
    }
}
