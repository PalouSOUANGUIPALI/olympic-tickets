package com.infoevent.olympictickets.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class OfferDto {
    private Long id;
    private String name;
    private int capacity;
    private BigDecimal price;
    private String description;
    private String includedActivitiesOffer; // Activités incluses pour chaque offre spécifique
    private String offerType; // Type d'offre: "Solo", "Duo", "Family"
    private LocalDateTime createdAt;

}
