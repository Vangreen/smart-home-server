package com.barszcz.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "BILLIONAIRES")
@Data
public class Bilionare {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    String name;
}
