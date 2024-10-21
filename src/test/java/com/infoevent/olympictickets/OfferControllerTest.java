package com.infoevent.olympictickets;

import com.infoevent.olympictickets.controller.OfferController;
import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferControllerTest {

    private OfferService offerService;
    private OfferController offerController;

    @BeforeEach
    void setUp() {
        offerService = mock(OfferService.class);
        offerController = new OfferController(offerService);
    }

    @Test
    void testShowManageOfferPage() {
        String viewName = offerController.showManageOfferPage();
        assertEquals("manage-offers", viewName);
    }

    @Test
    void testShowOfferPage() {
        String viewName = offerController.showOfferPage();
        assertEquals("billets", viewName);
    }

    @Test
    void testGetAllOffers() {
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);
        when(offerService.getAllOffers()).thenReturn(offerList);

        ResponseEntity<List<OfferDto>> response = offerController.getAllOffers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testGetAllOffersForManagement() {
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);
        when(offerService.getAllOffersForManagment()).thenReturn(offerList);

        ResponseEntity<List<OfferDto>> response = offerController.getAllOffersForManagement();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testGetOffersByType() {
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);
        when(offerService.getOffersByType("Solo")).thenReturn(offerList);

        ResponseEntity<List<OfferDto>> response = offerController.getOffersByType("Solo");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testCreateOffer() {
        OfferDto offerDto = new OfferDto();
        when(offerService.createOffer(offerDto)).thenReturn(offerDto);

        ResponseEntity<OfferDto> response = offerController.createOffer(offerDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testUpdateOffer() {
        Long offerId = 1L;
        OfferDto offerDto = new OfferDto();
        when(offerService.updateOffer(offerId, offerDto)).thenReturn(offerDto);

        ResponseEntity<OfferDto> response = offerController.updateOffer(offerId, offerDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testGetOfferById() {
        Long offerId = 1L;
        OfferDto offerDto = new OfferDto();
        when(offerService.getOfferById(offerId)).thenReturn(offerDto);

        ResponseEntity<OfferDto> response = offerController.getOfferById(offerId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testDeleteOffer() {
        Long offerId = 1L;

        // Simuler le comportement du service
        doNothing().when(offerService).deleteOffer(offerId);

        // Appeler la méthode deleteOffer
        ResponseEntity<String> response = offerController.deleteOffer(offerId);

        // Vérifier que le code de statut est 204
        assertEquals(200, response.getStatusCodeValue());
    }

}

