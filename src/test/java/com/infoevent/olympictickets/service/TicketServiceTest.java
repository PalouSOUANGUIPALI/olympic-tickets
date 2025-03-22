package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.TicketDto;
import com.infoevent.olympictickets.entity.*;
import com.infoevent.olympictickets.exception.TicketNotFoundException;
import com.infoevent.olympictickets.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock private OfferRepository offerRepository;
    @Mock private UserRepository userRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private EmailService emailService;

    @InjectMocks private TicketService ticketService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : TicketService et dépendances mockées.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Nettoyage terminé avec succès.\n");
    }

    @Test
    void testGenerateSecurityKey() {
        // Étape 1 : Appel de la méthode pour générer une clé
        String key = ticketService.generateSecurityKey();

        // Étape 2 : Vérifier que la clé n'est pas nulle et suffisamment longue
        assertNotNull(key);
        assertTrue(key.length() > 10);
        System.out.println("RÉSULTAT : testGenerateSecurityKey est passé avec succès.");
    }

    @Test
    void testGenerateQrCode() {
        // Étape 1 : Créer une chaîne pour le QR code
        String data = "Test QR Data";

        // Étape 2 : Générer le QR code
        byte[] qrBytes = ticketService.generateQrCode(data);

        // Étape 3 : Vérifier le résultat
        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);
        System.out.println("RÉSULTAT : testGenerateQrCode est passé avec succès.");
    }


    @Test
    void testReserveTicket_success() {
        // Étape 1 : Préparer les entités simulées
        TicketDto dto = new TicketDto();
        dto.setUserId(1);
        dto.setOfferId(2L);

        User user = new User();
        user.setId(1);
        user.setSecurityKey("SEC123");

        SoloOffer offer = new SoloOffer();
        offer.setId(2L);
        offer.setPrice(BigDecimal.valueOf(100));

        // Étape 2 : Simuler les retours des repositories
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(offerRepository.findById(2L)).thenReturn(Optional.of(offer));
        when(ticketRepository.save(any())).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setId(1L);
            ticket.setUser(user);
            ticket.setOffer(offer);
            return ticket;
        });

        // Étape 3 : Appeler la méthode à tester
        TicketDto ticketDto = ticketService.reserveTicket(dto);

        // Étape 4 : Vérifier les résultats
        assertEquals(1L, ticketDto.getId());
        assertEquals(1L, (long) ticketDto.getUserId()); // <-- fix ici
        assertEquals(2L, (long) ticketDto.getOfferId());
        assertEquals("Solo", ticketDto.getOfferType());

        assertNotNull(ticketDto.getQrCode());
        System.out.println("RÉSULTAT : testReserveTicket_success est passé avec succès.");
    }

    @Test
    void testConfirmTicketPurchase_success() {
        // Étape 1 : Simuler un ticket et utilisateur associé
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        ticket.setOfferType("Duo");

        Offer offer = new DuoOffer();
        offer.setPrice(BigDecimal.valueOf(200));
        ticket.setOffer(offer);

        User user = new User();
        user.setEmail("client@test.com");
        ticket.setUser(user);
        ticket.setQrCode("qr_path.png");

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        // Étape 2 : Appeler la méthode
        ticketService.confirmTicketPurchase(10L);

        // Étape 3 : Vérifier que l'e-mail a été envoyé
        verify(emailService).sendTicketPurchaseConfirmation("client@test.com", "Duo", BigDecimal.valueOf(200), "qr_path.png");
        System.out.println("RÉSULTAT : testConfirmTicketPurchase_success est passé avec succès.");
    }

    @Test
    void testConfirmTicketPurchase_ticketNotFound() {
        // Étape 1 : Simuler un ticket non trouvé
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // Étape 2 : Vérifier que l'exception est levée
        assertThrows(TicketNotFoundException.class, () -> ticketService.confirmTicketPurchase(999L));
        System.out.println("RÉSULTAT : testConfirmTicketPurchase_ticketNotFound est passé avec succès.");
    }

    @Test
    void testGetAllTickets() {
        // Étape 1 : Préparer une liste fictive de tickets
        List<Ticket> tickets = List.of(new Ticket(), new Ticket());
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Étape 2 : Appeler la méthode
        List<Ticket> result = ticketService.getAllTickets();

        // Étape 3 : Vérifier la taille
        assertEquals(2, result.size());
        System.out.println("RÉSULTAT : testGetAllTickets est passé avec succès.");
    }
}
