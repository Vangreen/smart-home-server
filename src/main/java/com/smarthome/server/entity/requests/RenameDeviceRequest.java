package com.smarthome.server.entity.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RenameDeviceRequest {
    private int deviceSerial;
    private String newDeviceName;
}
