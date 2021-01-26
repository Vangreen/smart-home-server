package com.barszcz.server;

import com.barszcz.server.dao.DeviceConfigurationDao;
import com.barszcz.server.entity.DeviceConfigurationModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(DeviceConfigurationDao dao){
//        return(args) ->{
//            DeviceConfigurationModel configurationModel = new DeviceConfigurationModel();
//            configurationModel.setSerial(123457);
//            configurationModel.setIp("ws://192.168.2.145:81");
//            configurationModel.setDeviceName("biurko");
//            configurationModel.setHue(100);
//            configurationModel.setSat(50);
//            configurationModel.setBright(50);
//            configurationModel.setDeviceState("off");
//            configurationModel.setRoom("pawla");
//            configurationModel.setDeviceType("ledrgb");
//            dao.save(configurationModel);
//        };
//    }

//    @Bean
//    public CommandLineRunner roomDemo(RoomsDao dao){
//        return (args) ->{
//            RoomModel roomModel = new RoomModel();
//            roomModel.setRoom("salon");
//            dao.save(roomModel);
//            RoomModel roomModel1 = new RoomModel();
//            roomModel1.setRoom("kuchnia");
//            dao.save(roomModel1);
//        };
//    }
}

