package com.barszcz.server;

import com.barszcz.server.dao.ConfigurationDao;
import com.barszcz.server.dao.RoomsDao;
import com.barszcz.server.entity.ConfigurationModel;
import com.barszcz.server.entity.RoomModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(ConfigurationDao dao){
//        return(args) ->{
//            ConfigurationModel configurationModel = new ConfigurationModel();
//            configurationModel.setIp("192.168.1.2");
//            configurationModel.setDeviceName("alpawla");
//            configurationModel.setRed(111);
//            configurationModel.setGreen(222);
//            configurationModel.setBlue(333);
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
