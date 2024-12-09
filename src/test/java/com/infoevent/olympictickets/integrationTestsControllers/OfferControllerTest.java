package com.infoevent.olympictickets.integrationTestsControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoevent.olympictickets.controller.OfferController;
import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
public class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OfferService offerService;

    private OfferDto offerDto;

    @BeforeEach
    public void setUp() {
        // Initialiser un OfferDto pour les tests
        offerDto = new OfferDto();
        offerDto.setId(1L);
        offerDto.setName("Test Offer");
        offerDto.setCapacity(10);
        offerDto.setPrice(BigDecimal.valueOf(100.0));
        offerDto.setDescription("Test description");
        offerDto.setOfferType("Solo");
        offerDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetAllOffers() throws Exception {
        // Simuler le retour du service avec une liste d'offres
        when(offerService.getAllOffers()).thenReturn(List.of(offerDto));

        // Appeler l'endpoint GET /offers/allOffers
        mockMvc.perform(get("/offers/allOffers"))
                .andExpect(status().isOk()) // Vérifier que la réponse HTTP est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Vérifier que le type de contenu est JSON
                .andExpect(jsonPath("$[0].id").value(1)) // Vérifier que le premier élément a un ID égal à 1
                .andExpect(jsonPath("$[0].name").value("Test Offer")) // Vérifier le nom de l'offre
                .andExpect(jsonPath("$[0].capacity").value(10)) // Vérifier la capacité de l'offre
                .andExpect(jsonPath("$[0].price").value(100.0)) // Vérifier le prix
                .andExpect(jsonPath("$[0].offerType").value("Solo")); // Vérifier le type de l'offre
    }

    @Test
    public void testGetOfferById() throws Exception {
        // Simuler le retour du service pour une offre spécifique
        when(offerService.getOfferById(1L)).thenReturn(offerDto);

        // Appeler l'endpoint GET /offers/get-offer/{id}
        mockMvc.perform(get("/offers/get-offer/1"))
                .andExpect(status().isOk()) // Vérifier que la réponse HTTP est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Vérifier que le type de contenu est JSON
                .andExpect(jsonPath("$.id").value(1)) // Vérifier l'ID de l'offre
                .andExpect(jsonPath("$.name").value("Test Offer")) // Vérifier le nom de l'offre
                .andExpect(jsonPath("$.capacity").value(10)) // Vérifier la capacité
                .andExpect(jsonPath("$.price").value(100.0)) // Vérifier le prix
                .andExpect(jsonPath("$.offerType").value("Solo")); // Vérifier le type
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATEUR") // Utilisation d'un utilisateur mocké avec le rôle "ADMINISTRATEUR"
    public void testGetAllOffersForManagement() throws Exception {
        // Simuler le retour du service avec une liste d'offres
        when(offerService.getAllOffersForManagment()).thenReturn(List.of(offerDto));

        // Appeler l'endpoint GET /offers/allOffers-manage
        mockMvc.perform(get("/offers/allOffers-manage"))
                .andExpect(status().isOk()) // Vérifier que la réponse HTTP est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Vérifier que le type de contenu est JSON
                .andExpect(jsonPath("$[0].id").value(1)) // Vérifier l'ID de l'offre
                .andExpect(jsonPath("$[0].name").value("Test Offer")) // Vérifier le nom de l'offre
                .andExpect(jsonPath("$[0].capacity").value(10)) // Vérifier la capacité
                .andExpect(jsonPath("$[0].price").value(100.0)) // Vérifier le prix
                .andExpect(jsonPath("$[0].offerType").value("Solo")); // Vérifier le type
    }

    @Test
    public void testCreateOffer() throws Exception {
        // Créer une offre DTO pour tester la création
        OfferDto newOfferDto = new OfferDto();
        newOfferDto.setName("New Test Offer");
        newOfferDto.setCapacity(15);
        newOfferDto.setPrice(BigDecimal.valueOf(200.0));
        newOfferDto.setDescription("New Test Offer description");
        newOfferDto.setOfferType("Solo");

        OfferDto savedOfferDto = new OfferDto();
        savedOfferDto.setId(2L);
        savedOfferDto.setName("New Test Offer");
        savedOfferDto.setCapacity(15);
        savedOfferDto.setPrice(BigDecimal.valueOf(200.0));
        savedOfferDto.setDescription("New Test Offer description");
        savedOfferDto.setOfferType("Solo");

        // Simuler le service pour créer une nouvelle offre
        when(offerService.createOffer(newOfferDto)).thenReturn(savedOfferDto);

        // Appeler l'endpoint POST /offers/create
        mockMvc.perform(post("/offers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newOfferDto)))
                .andExpect(status().isOk()) // Vérifier que la réponse HTTP est 200 OK
                .andExpect(jsonPath("$.id").value(2)) // Vérifier que l'ID de l'offre créée est bien 2
                .andExpect(jsonPath("$.name").value("New Test Offer")) // Vérifier le nom de l'offre
                .andExpect(jsonPath("$.capacity").value(15)) // Vérifier la capacité
                .andExpect(jsonPath("$.price").value(200.0)) // Vérifier le prix
                .andExpect(jsonPath("$.offerType").value("Solo")); // Vérifier le type de l'offre
    }

    @Test
    public void testUpdateOffer() throws Exception {
        // Offres à mettre à jour
        OfferDto updatedOfferDto = new OfferDto();
        updatedOfferDto.setId(1L);
        updatedOfferDto.setName("Updated Test Offer");
        updatedOfferDto.setCapacity(20);
        updatedOfferDto.setPrice(BigDecimal.valueOf(150.0));
        updatedOfferDto.setDescription("Updated Test Offer description");
        updatedOfferDto.setOfferType("Solo");

        // Simuler le service pour la mise à jour d'une offre
        when(offerService.updateOffer(1L, updatedOfferDto)).thenReturn(updatedOfferDto);

        // Appeler l'endpoint PUT /offers/update/{id}
        mockMvc.perform(put("/offers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedOfferDto)))
                .andExpect(status().isOk()) // Vérifier que la réponse HTTP est 200 OK
                .andExpect(jsonPath("$.id").value(1)) // Vérifier l'ID de l'offre mise à jour
                .andExpect(jsonPath("$.name").value("Updated Test Offer")) // Vérifier le nom mis à jour
                .andExpect(jsonPath("$.capacity").value(20)) // Vérifier la capacité mise à jour
                .andExpect(jsonPath("$.price").value(150.0)) // Vérifier le prix mis à jour
                .andExpect(jsonPath("$.offerType").value("Solo")); // Vérifier le type mis à jour
    }

    @Test
    public void testDeleteOffer() throws Exception {
        // Appeler l'endpoint DELETE /offers/delete/{id}
        mockMvc.perform(delete("/offers/delete/1"))
                .andExpect(status().isOk()); // Vérifier que la réponse HTTP est 200 OK
    }
}
