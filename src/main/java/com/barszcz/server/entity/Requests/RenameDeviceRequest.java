package com.barszcz.server.entity.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameDeviceRequest {
    public int deviceSerial;
    public String newDeviceName;
}
