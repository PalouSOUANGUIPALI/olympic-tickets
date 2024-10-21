package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Instant createdAt;
    private Instant expirationTime;
    private Instant activationTime;
    private String code;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    private User user;
}
