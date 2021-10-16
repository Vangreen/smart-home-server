package com.smarthome.server.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "unassignedDevices")
@Data
public class UnassignedDeviceModel {
    @Id
    @Column(name = "serial", nullable = false)
    int serial;

    @Column(name = "device_type", nullable = false)
    String deviceType;
}
