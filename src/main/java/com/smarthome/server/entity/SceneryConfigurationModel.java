package com.smarthome.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Data
public class SceneryConfigurationModel {

    @Id
    private int id;
    //    private int userID;
    private int roomID;
    private String sceneryName;
    private String logo;
    private String sceneryStatus;

}
