package com.smarthome.server.entity.responses;

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

    public ColorChangeResponse(String status, int hue, int bright, int saturation) {
        this.task = "color change";
        this.status = status;
        this.hue = hue;
        this.brightness = bright;
        this.saturation = saturation;
    }
}

