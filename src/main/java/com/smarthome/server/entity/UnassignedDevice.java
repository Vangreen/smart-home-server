package com.smarthome.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Data
public class UnassignedDevice {

    @Id
    private int serial;
    private String deviceType;
}
