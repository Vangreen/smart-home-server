package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "respond")
@Data
public class Respond {
    @Id
    @Column(name = "respond")
    String respond;
    @Column(name = "admin")
    Boolean admin;
}
