package com.barszcz.server;

import com.barszcz.server.dao.BilionaireDao;
import com.barszcz.server.entity.Bilionare;
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
    public CommandLineRunner demo(BilionaireDao dao){
        return(args) ->{
            Bilionare bilionare = new Bilionare();
            bilionare.setName("bla1");
            dao.save(bilionare);
        };
    }
}
