package com.smarthome.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "deviceConfigurationInSceneries")
@Data
public class DeviceConfigurationInSceneryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    int id;

    @Column(name = "scenery_id", nullable = false)
    int sceneryID;

    @Column(name = "device_serial", nullable = false)
    int deviceSerial;

    @Column(name = "hue", nullable = false)
    int hue;

    @Column(name = "sat", nullable = false)
    int saturation;

    @Column(name = "bright", nullable = false)
    int brightness;

    @Column(name = "state", nullable = false)
    String deviceState;
}
