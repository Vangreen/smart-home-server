package com.smarthome.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class RoomConfigurationModel {

    @Id
    private int id;
    private String roomName;
    private String main;
}
