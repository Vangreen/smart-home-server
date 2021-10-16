package com.smarthome.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CONFIGURATION")
@Data
public class DeviceConfigurationModel {

    @Id
    @Column(name = "serial", nullable = false)
    int serial;

    @Column(name = "device_name", nullable = false)
    String deviceName;

    @Column(name = "hue", nullable = false)
    int hue;

    @Column(name = "sat", nullable = false)
    int saturation;

    @Column(name = "bright", nullable = false)
    int brightness;

    @Column(name = "state", nullable = false)
    String deviceStatus;

    @Column(name = "status", nullable = false)
    String deviceConnectionStatus;

    @Column(name = "roomID", nullable = false)
    int roomID;

    @Column(name = "device_type", nullable = false)
    String deviceType;

}
