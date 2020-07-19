package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "configuration")
@Data
public class ConfigurationModel {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    Long id;


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
}
