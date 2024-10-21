package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code", length = 2000)
    private String qrCode;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    // L'utilisateur qui a réservé le ticket
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // L'offre associée au ticket
    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = true)
    private Offer offer;

    // Type d'offre associée au ticket (Solo, Duo, Family)
    @Column(name = "offer_type", nullable = false)
    private String offerType;

    public Ticket() {
        this.createdAt = java.time.LocalDateTime.now(); // Initialisation de la date de création
    }



    // Getters et Setters generated with lombok


}

