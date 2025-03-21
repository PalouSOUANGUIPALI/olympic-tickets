package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.entity.PaymentResponse;
import com.infoevent.olympictickets.service.PaymentService;
import com.infoevent.olympictickets.controller.PaymentController.PaymentRequest;
import org.junit.jupiter.api.AfterEach;
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
        // Crée un mock du service de paiement
        paymentService = mock(PaymentService.class);

        // Injecte le mock dans le contrôleur
        paymentController = new PaymentController(paymentService);

        System.out.println("Initialisation du contrôleur avec le service paymentService mocké.");
    }

    @Test
    void testProcessPayment_Success() {
        System.out.println("TEST : testProcessPayment_Success est démarré.");

        // Préparation de la requête de paiement
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("4111111111111111");
        request.setExpiryDate("12/26");
        request.setCvc("123");
        request.setTicketIds("1,2,3");

        // Simuler une réponse du service de paiement (succès)
        PaymentResponse expectedResponse = new PaymentResponse(true, "Paiement réussi");
        when(paymentService.processPayment(
                request.getCardNumber(),
                request.getExpiryDate(),
                request.getCvc(),
                request.getTicketIds()
        )).thenReturn(expectedResponse);

        // Appeler la méthode du contrôleur
        PaymentResponse response = paymentController.processPayment(request);

        System.out.println("INFO : Réponse du service : " +
                "success=" + response.isSuccess() +
                ", message=" + response.getMessage()
        );

        // Vérifier le résultat
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Paiement réussi", response.getMessage());

        System.out.println("TEST : testProcessPayment_Success est terminé avec succès");
    }

    @Test
    void testProcessPayment_Failure() {
        System.out.println("TEST : testProcessPayment_Failure est démarré.");

        // Préparer une requête de paiement invalide
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("4111111111111111");
        request.setExpiryDate("12/26");
        request.setCvc("123");
        request.setTicketIds("1,2,3");

        // Simuler une exception lancée par le service de paiement
        when(paymentService.processPayment(
                request.getCardNumber(),
                request.getExpiryDate(),
                request.getCvc(),
                request.getTicketIds()
        )).thenThrow(new RuntimeException("Paiement refusé"));

        // Appeler la méthode du contrôleur
        PaymentResponse response = paymentController.processPayment(request);

        System.out.println("INFO : Réponse du contrôleur après échec : success=" +
                response.isSuccess() +
                ", message=" + response.getMessage()
        );

        // Vérifier que l'erreur est bien gérée
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Paiement refusé", response.getMessage());

        System.out.println("TEST : testProcessPayment_Failure terminé avec succès");
    }

    @Test
    void testShowPaiementPage() {
        System.out.println("TEST : testShowPaiementPage démarré.");

        // Simuler le modèle
        Model model = mock(Model.class);
        String ticketIds = "1,2,3";

        // Appeler la méthode
        String view = paymentController.showPaiementPage(ticketIds, model);

        System.out.println("INFO : Vue retournée : " + view);

        // Vérifier que l'attribut a bien été ajouté au modèle
        verify(model).addAttribute("ticketIds", ticketIds);

        // Vérifier que la vue retournée est correcte
        assertEquals("payment", view);

        System.out.println("TEST : testShowPaiementPage terminé avec succès");
    }

    @AfterEach
    void tearDown() {
        System.out.println("TEARDOWN : Test de la méthode terminé avec succès.\n");
    }

}
