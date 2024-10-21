package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "offers")
@Inheritance(strategy = InheritanceType.JOINED)
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int capacity;
    // Type d'offre
    @Column(name = "offer_type")
    private String offerType;

    @Column(name = "created_at", updatable = false) // Ne pas mettre à jour la date de création lors d'un update
    private LocalDateTime createdAt;

    //////////////////////// POINT D'EXPLICATIONS ///////////////////////////////

    // La suppression d'une offfe entrainerait la suppression des tickets associés car,
    // on ne doit supprimer une offre en vente si et seulment si elle n'est plus commercialisée
    // Et donc ces tickets ont déjà été utilisés lors de l'événement par ceux qui les ont achetés
    @OneToMany(mappedBy = "offer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();


    // Getters et Setters générés par lombok
}