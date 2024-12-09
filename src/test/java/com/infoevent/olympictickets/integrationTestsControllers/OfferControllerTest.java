package com.infoevent.olympictickets.integrationTestsControllers;

import com.infoevent.olympictickets.controller.OfferController;
import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        // Préparer les mocks et initialiser le contrôleur pour les tests d'intégration
        offerService = mock(OfferService.class);
        offerController = new OfferController(offerService);
    }

    @Test
    void testShowManageOfferPage() {
        // Vérifie que la méthode retourne la vue correcte pour gérer les offres
        String viewName = offerController.showManageOfferPage();
        assertEquals("manage-offers", viewName);
    }

    @Test
    void testShowOfferPage() {
        // Vérifie que la méthode retourne la vue correcte pour afficher les billets
        String viewName = offerController.showOfferPage();

        // Vérifier le résultat
        assertEquals("billets", viewName);
    }

    @Test
    void testGetAllOffers() {
        // Teste la récupération de toutes les offres
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);

        // Simuler le service pour retourner une liste d'offres
        when(offerService.getAllOffers()).thenReturn(offerList);

        // Appeler le contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getAllOffers();

        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testGetAllOffersForManagement() {
        // Teste la récupération des offres pour la gestion
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);

        // Simuler le service pour retourner une liste d'offres pour la gestion
        when(offerService.getAllOffersForManagment()).thenReturn(offerList);

        // Appeler le contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getAllOffersForManagement();


        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testGetOffersByType() {
        // Teste la récupération des offres par type
        OfferDto offerDto = new OfferDto();
        List<OfferDto> offerList = Collections.singletonList(offerDto);

        // Simuler le service pour retourner des offres de type "Solo"
        when(offerService.getOffersByType("Solo")).thenReturn(offerList);

        // Appeler le contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getOffersByType("Solo");

        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(offerDto, response.getBody().get(0));
    }

    @Test
    void testCreateOffer() {
        // Teste la création d'une nouvelle offre
        OfferDto offerDto = new OfferDto();

        // Simuler le service pour retourner l'offre créée
        when(offerService.createOffer(offerDto)).thenReturn(offerDto);

        // Appeler le contrôleur
        ResponseEntity<OfferDto> response = offerController.createOffer(offerDto);

        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testUpdateOffer() {
        // Teste la mise à jour d'une offre existante
        Long offerId = 1L;
        OfferDto offerDto = new OfferDto();

        // Simuler le service pour retourner l'offre mise à jour
        when(offerService.updateOffer(offerId, offerDto)).thenReturn(offerDto);

        // Appeler le contrôleur
        ResponseEntity<OfferDto> response = offerController.updateOffer(offerId, offerDto);

        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testGetOfferById() {
        // Teste la récupération d'une offre par son ID
        Long offerId = 1L;
        OfferDto offerDto = new OfferDto();

        // Simuler le service pour retourner l'offre correspondante
        when(offerService.getOfferById(offerId)).thenReturn(offerDto);

        // Appeler le contrôleur
        ResponseEntity<OfferDto> response = offerController.getOfferById(offerId);

        // Vérifier le résultat
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offerDto, response.getBody());
    }

    @Test
    void testDeleteOffer() {
        // Teste la suppression d'une offre par son ID
        Long offerId = 1L;

        // Simuler le comportement du service de suppression
        doNothing().when(offerService).deleteOffer(offerId);

        // Appeler la méthode de suppression
        ResponseEntity<String> response = offerController.deleteOffer(offerId);

        // Vérifier que la réponse indique une réussite (code 200)
        assertEquals(200, response.getStatusCodeValue());
    }
}
