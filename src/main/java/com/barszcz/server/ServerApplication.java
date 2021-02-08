package com.barszcz.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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

}

