package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.entity.PaymentResponse;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private PaymentService paymentService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : PaymentService et dépendances mockées.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Test et Nettoyage terminés avec succès.\n");
    }

    @Test
    void testProcessPayment_validData_shouldSucceed() {
        // Étape 1 : Préparer des données de paiement valides
        String cardNumber = "1234567812345678";
        String expiryDate = "12/25";
        String cvc = "123";
        String ticketIds = "1,2,3";

        // Étape 2 : Simuler le comportement du ticketService
        doNothing().when(ticketService).confirmTicketPurchase(anyLong());

        // Étape 3 : Appeler la méthode à tester
        PaymentResponse response = paymentService.processPayment(cardNumber, expiryDate, cvc, ticketIds);

        // Étape 4 : Vérification
        assertTrue(response.isSuccess());
        assertEquals("Paiement réussi", response.getMessage());

        // Étape 5 : Vérifier que confirmTicketPurchase a bien été appelé 3 fois
        verify(ticketService, times(3)).confirmTicketPurchase(anyLong());
        System.out.println("RÉSULTAT : testProcessPayment_validData_shouldSucceed est passé avec succès : " +
                response.getMessage());
    }

    @Test
    void testProcessPayment_invalidCard_shouldFail() {
        // Étape 1 : Préparer des données de carte non valides
        String cardNumber = "12345678";  // Trop court
        String expiryDate = "12/25";
        String cvc = "12"; // Trop court
        String ticketIds = "1";

        // Étape 2 : Appeler la méthode à tester
        PaymentResponse response = paymentService.processPayment(cardNumber, expiryDate, cvc, ticketIds);

        // Étape 3 : Vérification
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Échec du paiement"));
        verify(ticketService, never()).confirmTicketPurchase(anyLong());
        System.out.println("RÉSULTAT : testProcessPayment_invalidCard_shouldFail est passé avec succès : " +
                response.getMessage());
    }
}
