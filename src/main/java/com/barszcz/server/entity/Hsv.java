package com.barszcz.server.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hsv {

    int hue;
    int saturation;
    int bright;
}
