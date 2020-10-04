package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "rooms")
@Data
public class RoomRespondModel {
    @Id
    @Column(name = "room")
    String room;

    @Column(name="room_type")
    String room_type;
}
