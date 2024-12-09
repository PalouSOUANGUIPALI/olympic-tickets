package com.infoevent.olympictickets.integrationTestsControllers;

import com.infoevent.olympictickets.controller.PaymentController;
import com.infoevent.olympictickets.controller.PaymentController.PaymentRequest;
import com.infoevent.olympictickets.entity.PaymentResponse;
import com.infoevent.olympictickets.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks et du contrôleur pour les tests d'intégration
        paymentService = mock(PaymentService.class);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void testProcessPaymentSuccess() {
        // Simuler une requête de paiement valide
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCardNumber("4111111111111111");
        paymentRequest.setExpiryDate("12/25");
        paymentRequest.setCvc("123");
        paymentRequest.setTicketIds("1,2,3");

        // Simuler une réponse réussie du service de paiement
        PaymentResponse expectedResponse = new PaymentResponse(true, "Payment successful");
        when(paymentService.processPayment("4111111111111111", "12/25", "123", "1,2,3"))
                .thenReturn(expectedResponse);

        // Appeler la méthode du contrôleur
        PaymentResponse response = paymentController.processPayment(paymentRequest);

        // Vérifier le résultat
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Payment successful", response.getMessage());
    }

    @Test
    void testProcessPaymentFailure() {
        // Simuler une requête de paiement invalide
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCardNumber("4111111111111111");
        paymentRequest.setExpiryDate("12/25");
        paymentRequest.setCvc("123");
        paymentRequest.setTicketIds("1,2,3");

        // Simuler une exception du service de paiement
        when(paymentService.processPayment("4111111111111111", "12/25", "123", "1,2,3"))
                .thenThrow(new RuntimeException("Payment failed"));

        // Appeler la méthode du contrôleur
        PaymentResponse response = paymentController.processPayment(paymentRequest);

        // Vérifier le résultat
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Payment failed", response.getMessage());
    }

    @Test
    void testShowPaiementPage() {
        // Simuler les paramètres et le modèle
        String ticketIds = "1,2,3";
        Model model = mock(Model.class);

        // Appeler la méthode du contrôleur
        String viewName = paymentController.showPaiementPage(ticketIds, model);

        // Vérifier que les IDs des tickets ont été ajoutés au modèle
        verify(model, times(1)).addAttribute("ticketIds", ticketIds);

        // Vérifier que le nom de la vue est correct
        assertEquals("payment", viewName);
    }
}

