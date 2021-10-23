package com.smarthome.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Device {

    @Id
    private int serial;
    private String deviceName;
    private int hue;
    private int saturation;
    private int brightness;
    private String deviceStatus;
    private String deviceConnectionStatus;
    private int roomID;
    private String deviceType;
}
