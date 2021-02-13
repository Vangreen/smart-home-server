package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "roomConfiguration")
@Data
public class RoomConfigurationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    int id;

    @Column(name = "room_name", nullable = false)
    String roomName;

    @Column(name = "main", nullable = false)
    String main;
}
