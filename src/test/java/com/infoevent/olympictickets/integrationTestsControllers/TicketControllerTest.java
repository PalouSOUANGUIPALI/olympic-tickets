package com.infoevent.olympictickets.integrationTestsControllers;

import com.infoevent.olympictickets.controller.TicketController;
import com.infoevent.olympictickets.dto.TicketDto;
import com.infoevent.olympictickets.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    private TicketService ticketService;
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks et du contrôleur pour les tests d'intégration
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
    }

    @Test
    void testReserveTicket() {
        // Préparer un TicketDto simulé pour la réservation
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1L);
        ticketDto.setUserId(1001);
        ticketDto.setOfferId(2001L);
        ticketDto.setQrCode("QRCODE123");
        ticketDto.setOfferType("solo");
        ticketDto.setCreatedAt(LocalDateTime.now());

        // Simuler la réponse du service avec un ticket réservé
        when(ticketService.reserveTicket(ticketDto)).thenReturn(ticketDto);

        // Appeler la méthode du contrôleur
        ResponseEntity<TicketDto> response = ticketController.reserveTicket(ticketDto);

        // Vérifications des résultats
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        TicketDto responseBody = response.getBody();

        // Vérifier les propriétés du ticket dans la réponse
        assertEquals(ticketDto.getId(), responseBody.getId());
        assertEquals(ticketDto.getUserId(), responseBody.getUserId());
        assertEquals(ticketDto.getOfferId(), responseBody.getOfferId());
        assertEquals(ticketDto.getQrCode(), responseBody.getQrCode());
        assertEquals(ticketDto.getOfferType(), responseBody.getOfferType());
        assertEquals(ticketDto.getCreatedAt(), responseBody.getCreatedAt());

        // Vérifier que le service a été appelé une fois avec le bon argument
        verify(ticketService, times(1)).reserveTicket(ticketDto);
    }
}
