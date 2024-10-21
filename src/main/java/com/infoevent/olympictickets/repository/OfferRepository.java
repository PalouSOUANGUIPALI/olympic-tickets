package com.infoevent.olympictickets.repository;

import com.infoevent.olympictickets.entity.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends CrudRepository<Offer, Long> {

    // Méthode pour vérifier si une offre existe déjà par son nom
    boolean existsByName(String name);

    // Rechercher une offre selon le type
    List<Offer> findAllByOfferType(String offerType);
}
