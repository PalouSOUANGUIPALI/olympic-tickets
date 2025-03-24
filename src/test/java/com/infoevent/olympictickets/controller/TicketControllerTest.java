package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.dto.TicketDto;
import com.infoevent.olympictickets.service.TicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    private TicketService ticketService;         // Mock du service ticketService
    private TicketController ticketController;   // Contrôleur à tester

    @BeforeEach
    void setUp() {
        // Étape 1 : Initialisation des mocks et du contrôleur
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
        System.out.println("INITIALISATION: Contrôleur initialisé avec le service ticketService mocké.");
    }

    @AfterEach
    void tearDown() {
        // Étape finale après chaque test pour affichage de suivi
        System.out.println("TEARDOWN : Test de la méthode terminé avec succès.\n");
    }

    @Test
    void testReserveTicket() {
        System.out.println("TEST : testReserveTicket démarré.");

        // Étape 2 : Préparer l'objet ticket d'entrée
        TicketDto inputTicket = new TicketDto();
        inputTicket.setUserId(1001);
        inputTicket.setOfferId(2001L);
        inputTicket.setOfferType("Solo");
        inputTicket.setCreatedAt(LocalDateTime.now());

        // Étape 3 : Préparer la réponse attendue du service
        TicketDto returnedTicket = new TicketDto();
        returnedTicket.setId(1L);
        returnedTicket.setUserId(inputTicket.getUserId());
        returnedTicket.setOfferId(inputTicket.getOfferId());
        returnedTicket.setOfferType(inputTicket.getOfferType());
        returnedTicket.setCreatedAt(inputTicket.getCreatedAt());

        // Étape 4 : Simulation du comportement du service
        when(ticketService.reserveTicket(inputTicket)).thenReturn(returnedTicket);

        // Étape 5 : Appel réel de la méthode du contrôleur
        ResponseEntity<TicketDto> response = ticketController.reserveTicket(inputTicket);

        // Étape 6 : Vérification des résultats
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value()); // Le contrôleur retourne un 200, donc (OK)
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Solo", response.getBody().getOfferType());

        // Étape 7 : Vérifier que le service a bien été appelé avec les bons paramètres
        verify(ticketService, times(1)).reserveTicket(inputTicket);

        System.out.println("INFO : Ticket réservé avec ID=" + response.getBody().getId() + " et type=" + response.getBody().getOfferType());
        System.out.println("TEST : testReserveTicket terminé avec succès.");
    }
}
