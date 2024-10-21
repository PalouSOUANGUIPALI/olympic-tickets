package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FamilyOffer extends Offer {

    private String includedActivitiesOffer;

    // Getters et Setters
}
