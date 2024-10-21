package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
public class SoloOffer extends Offer {

    private String includedActivitiesOffer;

    // Getters et Setters
}
