package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.entity.*;
import com.infoevent.olympictickets.repository.OfferRepository;
import com.infoevent.olympictickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private OfferService offerService;

    private OfferDto offerDto;

    @BeforeEach
    public void setUp() {
        // Création d'un objet DTO pour les tests
        offerDto = new OfferDto();
        offerDto.setId(1L);
        offerDto.setName("Test Offer");
        offerDto.setDescription("Test description");
        offerDto.setPrice(BigDecimal.valueOf(100.0));
        offerDto.setCapacity(10);
        offerDto.setOfferType("Solo");
    }

    @Test
    public void testGetAllOffers() {
        // Créer une liste d'offres
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Offer");
        offer.setDescription("Test description");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(10);
        offer.setOfferType("Solo");

        List<Offer> offers = Collections.singletonList(offer);

        // Simuler le comportement du repository
        when(offerRepository.findAll()).thenReturn(offers);

        // Appel de la méthode à tester
        List<OfferDto> result = offerService.getAllOffers();

        // Vérifications
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Solo Offer", result.get(0).getName());
        verify(offerRepository, times(1)).findAll(); // Vérifier qu'on a appelé `findAll` une fois
    }

    @Test
    public void testGetOfferById() {
        // Création d'une offre simulée
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Offer");
        offer.setDescription("Test description");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(10);
        offer.setOfferType("Solo");

        // Simuler le comportement du repository
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Appel de la méthode à tester
        OfferDto result = offerService.getOfferById(1L);

        // Vérifications
        assertNotNull(result);
        assertEquals("Solo Offer", result.getName());
        assertEquals("Test description", result.getDescription());
        verify(offerRepository, times(1)).findById(1L); // Vérifier qu'on a appelé `findById` une fois
    }

    @Test
    public void testCreateOffer() {
        // Création de l'objet offer
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Offer");
        offer.setDescription("Test description");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(10);
        offer.setOfferType("Solo");

        // Simuler le comportement du repository
        when(offerRepository.existsByName(offerDto.getName())).thenReturn(false);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Appel de la méthode à tester
        OfferDto result = offerService.createOffer(offerDto);

        // Vérifications
        assertNotNull(result);
        assertEquals("Solo Offer", result.getName());
        assertEquals(100.0, result.getPrice());
        verify(offerRepository, times(1)).existsByName(offerDto.getName()); // Vérifier qu'on a appelé `existsByName` une fois
        verify(offerRepository, times(1)).save(any(Offer.class)); // Vérifier qu'on a appelé `save` une fois
    }

    @Test
    public void testUpdateOffer() {
        // Création de l'objet offer
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Offer");
        offer.setDescription("Test description");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(10);
        offer.setOfferType("Solo");

        OfferDto updatedOfferDto = new OfferDto();
        updatedOfferDto.setId(1L);
        updatedOfferDto.setName("Updated Solo Offer");
        updatedOfferDto.setDescription("Updated description");
        updatedOfferDto.setPrice(BigDecimal.valueOf(120.0));
        updatedOfferDto.setCapacity(15);
        updatedOfferDto.setOfferType("Solo");

        // Simuler le comportement du repository
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Appel de la méthode à tester
        OfferDto result = offerService.updateOffer(1L, updatedOfferDto);

        // Vérifications
        assertNotNull(result);
        assertEquals("Updated Solo Offer", result.getName());
        assertEquals(120.0, result.getPrice());
        verify(offerRepository, times(1)).save(any(Offer.class)); // Vérifier qu'on a appelé `save` une fois
    }

    @Test
    public void testGetOffersSoldByType() {
        // Création d'une offre simulée
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Offer");
        offer.setDescription("Test description");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(10);
        offer.setOfferType("Solo");

        Ticket ticket = new Ticket();
        ticket.setOffer(offer);

        List<Ticket> tickets = Collections.singletonList(ticket);

        // Simuler le comportement des repositories
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Appel de la méthode à tester
        Map<String, List<OfferDto>> result = offerService.getOffersSoldByType();

        // Vérifications
        assertNotNull(result);
        assertTrue(result.containsKey("Solo"));
        assertEquals(1, result.get("Solo").size());
        verify(ticketRepository, times(1)).findAll(); // Vérifier qu'on a appelé `findAll` une fois
    }

    @Test
    public void testDeleteOffer() {
        // Création de l'objet Offer
        Offer offer = new SoloOffer();
        offer.setId(1L);

        Ticket ticket = new Ticket();
        ticket.setOffer(offer);

        // Simuler le comportement des repositories
        when(ticketRepository.findByOfferId(1L)).thenReturn(Collections.singletonList(ticket));
        doNothing().when(ticketRepository).deleteAll(anyList());
        doNothing().when(offerRepository).deleteById(1L);

        // Appel de la méthode à tester
        offerService.deleteOffer(1L);

        // Vérifications
        verify(ticketRepository, times(1)).findByOfferId(1L); // Vérifier qu'on a appelé `findByOfferId` une fois
        verify(ticketRepository, times(1)).deleteAll(anyList()); // Vérifier qu'on a appelé `deleteAll` une fois
        verify(offerRepository, times(1)).deleteById(1L); // Vérifier qu'on a appelé `deleteById` une fois
    }

    // Ajoutez d'autres tests pour les cas exceptionnels comme `AccessDeniedException` et `RuntimeException`
}
