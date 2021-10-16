package com.smarthome.server.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SceneryCreation {
    private String sceneryName;
    private String sceneryLogo;
    private int roomID;
    private List<DeviceConfigurationModel> devices;
}
