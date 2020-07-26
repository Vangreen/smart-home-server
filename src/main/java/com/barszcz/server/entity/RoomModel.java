package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "rooms")
@Data
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    int id;

    @Column(name = "room")
    String room;
}
