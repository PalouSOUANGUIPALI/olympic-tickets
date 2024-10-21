package com.infoevent.olympictickets.repository;

import com.infoevent.olympictickets.entity.Ticket;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {
    List<Ticket> findByUserId(int user_id);

    @Modifying
    @Query("UPDATE Ticket t SET t.offer = NULL WHERE t.offer.id = :offerId")
    void updateOfferIdToNull(@Param("offerId") Long offerId);


    List<Ticket> findByOfferId(Long offerId);

    //Méthode pour trouver tous les tickets associés à un type d'offre
    List<Ticket> findByOfferOfferType(String offerType);

}

