package com.barszcz.server.entity.Requests;

import lombok.Data;

@Data
public class RenameDeviceRequest {
    public int deviceSerial;
    public String newDeviceName;
}
