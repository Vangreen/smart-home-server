package com.smarthome.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "sceneryConfiguration")
@Data
public class SceneryConfigurationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    int id;

//    @Column(name = "user_id", nullable = false)
//    int userID;

    @Column(name = "room_id", nullable = false)
    int roomID;

    @Column(name = "scenery_name", nullable = false)
    String sceneryName;

    @Column(name = "logo", nullable = false)
    String logo;

    @Column(name = "scenery_status", nullable = false)
    String sceneryStatus;

}
