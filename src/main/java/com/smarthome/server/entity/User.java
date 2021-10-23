package com.smarthome.server.entity;

import com.smarthome.server.entity.enums.Role;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User {

    private String name;
    private String password;
    private Role role;
}
