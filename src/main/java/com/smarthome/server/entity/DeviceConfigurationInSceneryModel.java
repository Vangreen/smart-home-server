package com.smarthome.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class DeviceConfigurationInSceneryModel {

    @Id
    private int id;
    private int sceneryID;
    private int deviceSerial;
    private int hue;
    private int saturation;
    private int brightness;
    private String status;
}
