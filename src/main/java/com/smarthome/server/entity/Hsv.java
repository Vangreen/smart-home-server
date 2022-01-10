package com.smarthome.server.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hsv {

    private int hue;
    private int saturation;
    private int bright;
}
