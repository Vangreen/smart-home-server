package com.smarthome.server.entity.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameDeviceRequest {
    private int deviceSerial;
    private String newDeviceName;
}
