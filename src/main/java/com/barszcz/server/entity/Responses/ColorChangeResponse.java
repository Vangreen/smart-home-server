package com.barszcz.server.entity.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorChangeResponse {
    private String task;
    private String status;
    private int hue;
    private int brightness;
    private int saturation;
    private String floatingStatus;
    private int floatingSpeed;

    public ColorChangeResponse(String status, int hue, int bright, int saturation, String floatingStatus, int floatingSpeed) {
        this.task = "color change";
        this.status = status;
        this.hue = hue;
        this.brightness = bright;
        this.saturation = saturation;
        this.floatingStatus = floatingStatus;
        this.floatingSpeed = floatingSpeed;
    }
}

