package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class UserSettingsRespondModel {

    @Id
    @Column(name = "login", nullable = false)
    String login;


    @Column(name = "admin", nullable = false)
    Boolean admin;
}

