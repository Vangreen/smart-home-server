package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sceneryConfiguration")
@Data
public class SceneryConfigurationModel {
    @Id
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

}
