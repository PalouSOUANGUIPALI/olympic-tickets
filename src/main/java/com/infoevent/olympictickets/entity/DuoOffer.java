package com.infoevent.olympictickets.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class DuoOffer extends Offer {

    private String includedActivitiesOffer;

    // Getters et Setters
}
