package com.infoevent.olympictickets.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TicketDto {
    private Long id;
    private Integer userId;      // ID de l'utilisateur qui a réservé le ticket
    private Long offerId;     // Identifiant de l'offre réservée
    private String qrCode;    // QR code généré pour le ticket
    private String offerType; // Type d'offre pour le ticket : "solo", "duo", "family"
    private LocalDateTime createdAt; // Date de création du ticket


}