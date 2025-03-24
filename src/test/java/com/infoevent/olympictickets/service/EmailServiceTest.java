package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.entity.Ticket;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.repository.TicketRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private EmailService emailService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : EmailService et dépendances mockées.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Nettoyage et test terminés avec succès.\n");
    }

    @Test
    void testSendValidationEmail_success() throws Exception {
        // Étape 1 : Préparer un utilisateur et sa validation
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Jean");

        Validation validation = new Validation();
        validation.setUser(user);
        validation.setCode("123456");

        // Étape 2 : Préparer un MimeMessage mocké
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Étape 3 : Appeler la méthode à tester
        assertDoesNotThrow(() -> emailService.sendValidationEmail(validation));
        System.out.println("RÉSULTAT : testSendValidationEmail_success est passé avec succès.");
    }

    @Test
    void testSendTicketPurchaseConfirmation_success() {
        // Étape 1 : Préparer les données
        String email = "acheteur@example.com";
        String offerType = "Solo";
        BigDecimal price = new BigDecimal("120.00");
        String qrPath = "src/test/resources/fake_qr_code.png";

        // Étape 2 : Simuler le mailSender
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Étape 3 : Appeler la méthode à tester
        assertDoesNotThrow(() -> emailService.sendTicketPurchaseConfirmation(email, offerType, price, qrPath));
        System.out.println("RÉSULTAT : testSendTicketPurchaseConfirmation_success est passé avec succès.");
    }

    @Test
    void testSendWeeklyEventReminders_withValidTicket() {
        // Étape 1 : Préparer un ticket valide
        User user = new User();
        user.setEmail("rappel_participant_evennement@example.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUser(user);
        ticket.setOfferType("Duo");
        ticket.setQrCode("src/test/resources/fake_qr_code.png");
        ticket.setOffer(new com.infoevent.olympictickets.entity.Offer());
        ticket.getOffer().setPrice(new BigDecimal("99.99"));

        // Étape 2 : Simuler le repository et mail Sender
        when(ticketRepository.findAll()).thenReturn(Collections.singletonList(ticket));
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Étape 3 : Appeler la méthode à tester
        assertDoesNotThrow(() -> emailService.sendWeeklyEventReminders());
        System.out.println("RÉSULTAT : testSendWeeklyEventReminders_withValidTicket est passé avec succès.");
    }
}