package com.smarthome.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class Room {

    @Id
    private int id;
    private String roomName;
    //TODO change to boolean
    private String main;
}
