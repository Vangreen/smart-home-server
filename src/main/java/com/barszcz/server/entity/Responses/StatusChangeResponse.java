package com.barszcz.server.entity.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeResponse {
    private String task;
    private String status;
    private String floatingStatus;
    private int floatingSpeed;

    public StatusChangeResponse(String status, String floatingStatus, int floatingSpeed) {
        this.task = "status change";
        this.status = status;
        this.floatingStatus = floatingStatus;
        this.floatingSpeed = floatingSpeed;
    }
}
