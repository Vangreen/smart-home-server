package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roomsConfigurationInSceneries")
@Data
public class RoomConfigurationInSceneryModel {
    @Id
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
