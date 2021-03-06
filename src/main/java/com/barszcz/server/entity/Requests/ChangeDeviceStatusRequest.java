package com.barszcz.server.entity.Requests;

import lombok.Data;

@Data
public class ChangeDeviceStatusRequest {
    private String status;
    private String floatingStatus;
    private int floatingSpeed;
}
