package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.service.OfferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferControllerTest {

    private OfferService offerService;
    private OfferController offerController;

    @BeforeEach
    void setUp() {
        // Initialisation du mock du service et du contrôleur avant chaque test
        offerService = mock(OfferService.class);
        offerController = new OfferController(offerService);
        System.out.println("Initialisation du contrôleur avec le service offerService mocké.");
    }

    @AfterEach
    void tearDown() {
        // Affichage de fin de test
        System.out.println("TEARDOWN : Test de la méthode terminé avec succès.\n");
    }

    @Test
    void testShowManageOfferPage() {
        System.out.println("TEST : testShowManageOfferPage est démarré.");

        // Appel de la méthode
        String view = offerController.showManageOfferPage();

        // Vérification que la vue retournée est bien celle attendue
        System.out.println("INFO :  Vue retournée : " + view);
        assertEquals("manage-offers", view);
    }

    @Test
    void testShowOfferPage() {
        System.out.println("TEST : testShowOfferPage est démarré.");

        // Appel de la méthode
        String view = offerController.showOfferPage();

        // Vérification du nom de la vue HTML
        System.out.println("INFO : Vue retournée : " + view);
        assertEquals("billets", view);
    }

    @Test
    void testGetAllOffers() {
        System.out.println("TEST : testGetAllOffers démarré.");

        // Préparation : création d'une liste fictive d'offres
        OfferDto dto = new OfferDto();
        List<OfferDto> offers = List.of(dto);

        // Simulation du comportement du service
        when(offerService.getAllOffers()).thenReturn(offers);

        // Appel de la méthode du contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getAllOffers();

        // Vérification du code HTTP et du contenu retourné
        System.out.println("INFO : Nombre d'offres retournées : " + response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offers, response.getBody());
    }

    @Test
    void testGetAllOffersForManagement() {
        System.out.println("TEST : testGetAllOffersForManagement est démarré.");

        // Préparation des données fictives
        OfferDto dto = new OfferDto();
        List<OfferDto> offers = List.of(dto);

        // Simulation du service
        when(offerService.getAllOffersForManagment()).thenReturn(offers);

        // Appel de la méthode du contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getAllOffersForManagement();

        // Vérification du résultat
        System.out.println("INFO : Nombre d'offres pour admin : " + response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offers, response.getBody());
    }

    @Test
    void testGetOffersByType() {
        System.out.println("TEST : testGetOffersByType est démarré.");

        // Préparation : type d'offre à tester
        String offerType = "Solo";
        OfferDto dto = new OfferDto();
        List<OfferDto> offers = List.of(dto);

        // Simulation du service pour ce type
        when(offerService.getOffersByType(offerType)).thenReturn(offers);

        // Appel de la méthode du contrôleur
        ResponseEntity<List<OfferDto>> response = offerController.getOffersByType(offerType);

        // Vérification
        System.out.println("INFO : Type : " + offerType + " | Nombre d'offres : " + response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offers, response.getBody());
    }

    @Test
    void testCreateOffer() {
        System.out.println("TEST : testCreateOffer est démarré.");

        // Préparation de l'objet à envoyer en création
        OfferDto input = new OfferDto();
        input.setName("Offre Olympique");
        input.setOfferType("Solo");

        // Réponse simulée du service
        OfferDto output = new OfferDto();
        output.setName("Offre Olympique");
        output.setOfferType("Solo");

        // Simulation de la création via le service
        when(offerService.createOffer(input)).thenReturn(output);

        // Appel du contrôleur
        ResponseEntity<OfferDto> response = offerController.createOffer(input);

        // Vérifications du retour
        System.out.println("INFO : Offre créée : " + response.getBody().getName() + " (" + response.getBody().getOfferType() + ")");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Offre Olympique", response.getBody().getName());
        assertEquals("Solo", response.getBody().getOfferType());
    }

    @Test
    void testUpdateOffer() {
        System.out.println("TEST : testUpdateOffer est démarré.");

        // Préparation des données
        Long id = 1L;
        OfferDto input = new OfferDto();
        OfferDto updated = new OfferDto();
        updated.setOfferType("Duo");

        // Simulation de mise à jour
        when(offerService.updateOffer(id, input)).thenReturn(updated);

        // Appel du contrôleur
        ResponseEntity<OfferDto> response = offerController.updateOffer(id, input);

        // Vérification
        System.out.println("INFO : Offre mise à jour : ID=" + id + " | Type=" + response.getBody().getOfferType());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Duo", response.getBody().getOfferType());
    }

    @Test
    void testGetOfferById() {
        System.out.println("TEST : testGetOfferById est démarré.");

        // Préparation d'une offre fictive
        Long id = 1L;
        OfferDto dto = new OfferDto();
        dto.setId(id);

        // Simulation de la récupération par ID
        when(offerService.getOfferById(id)).thenReturn(dto);

        // Appel du contrôleur
        ResponseEntity<OfferDto> response = offerController.getOfferById(id);

        // Vérification
        System.out.println("INFO : Offre récupérée ID=" + response.getBody().getId());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void testDeleteOffer() {
        System.out.println("TEST : testDeleteOffer est démarré.");

        // Préparation
        Long id = 1L;

        // Simulation de suppression sans erreur
        doNothing().when(offerService).deleteOffer(id);

        // Appel du contrôleur
        ResponseEntity<String> response = offerController.deleteOffer(id);

        // Vérification du message et appel au service
        System.out.println("INFO : Message de suppression : " + response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("L'offre a été supprimée avec succès.", response.getBody());
        verify(offerService, times(1)).deleteOffer(id);
    }

    @Test
    void testGetOffersSoldByTypeWithAllTypes() {
        System.out.println("TEST : testGetOffersSoldByTypeWithAllTypes est démarré.");

        // Préparation d'un Model mocké
        Model model = mock(Model.class);

        // Simulation des offres vendues par type, sans l'offre "Familiale"
        Map<String, List<OfferDto>> offersByType = new HashMap<>();
        offersByType.put("Solo", List.of(new OfferDto()));
        offersByType.put("Duo", List.of());

        // Simulation du service
        when(offerService.getOffersSoldByType()).thenReturn(offersByType);

        // Appel de la méthode
        String view = offerController.getOffersSoldByType(model);

        // Vérification de la vue et de l'attribut ajouté
        System.out.println("INFO : Vue retournée : " + view);
        assertEquals("sold-by-type", view);

        // Vérification que les trois types sont présents dans les statistiques
        verify(model).addAttribute(eq("offerStats"), argThat(stats -> {
            Map<String, Integer> map = (Map<String, Integer>) stats;
            System.out.println("DEBUG : Statistiques calculées : " + map);
            return map.containsKey("Solo") && map.containsKey("Duo") && map.containsKey("Familiale");
        }));
    }
}
