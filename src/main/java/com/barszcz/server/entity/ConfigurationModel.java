package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CONFIGURATION")
@Data
public class ConfigurationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    int id;


    @Column(name = "ip", nullable = false)
    String ip;

    @Column(name = "device_name", nullable = false)
    String deviceName;

    @Column(name = "red", nullable = false)
    int red;

    @Column(name = "green", nullable = false)
    int green;

    @Column(name = "blue", nullable = false)
    int blue;

    @Column(name = "state", nullable = false)
    String deviceState;

    @Column(name = "room", nullable = false)
    String room;

    @Column(name = "device_type", nullable = false)
    String deviceType;
}
