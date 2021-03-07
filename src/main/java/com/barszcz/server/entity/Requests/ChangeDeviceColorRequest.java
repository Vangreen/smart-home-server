package com.barszcz.server.entity.Requests;

import lombok.Data;

@Data
public class ChangeDeviceColorRequest {
    private String task;
    private String status;
    private int hue;
    private int saturation;
    private int brightness;
    private String floatingStatus;
    private int floatingSpeed;
}
