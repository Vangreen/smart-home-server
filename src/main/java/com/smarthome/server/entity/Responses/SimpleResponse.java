package com.smarthome.server.entity.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleResponse {
    private String response;

    public SimpleResponse(String response){
        this.response = response;
    }
}
