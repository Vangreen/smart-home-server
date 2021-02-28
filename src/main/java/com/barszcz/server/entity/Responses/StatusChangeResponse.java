package com.barszcz.server.entity.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeResponse {
    private String task;
    private String status;

    public StatusChangeResponse(String status) {
        this.task = "status change";
        this.status = status;
    }
}
