package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.entity.PaymentResponse;
import com.infoevent.olympictickets.service.PaymentService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/process")
    @ResponseBody // renvoie du JSON
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Appeler le service de paiement avec les informations fournies y compris les IDs des tickets dans le lien
            return paymentService.processPayment(
                    paymentRequest.getCardNumber(),
                    paymentRequest.getExpiryDate(),
                    paymentRequest.getCvc(),
                    paymentRequest.getTicketIds() // Passez les IDs des tickets au service
            );
        } catch (RuntimeException e) {
            // En cas d'erreur, retourner une réponse JSON avec l'erreur
            return new PaymentResponse(false, e.getMessage()); // Retourne l'erreur
        }
    }


    // Classe interne pour les données de la demande de paiement
    @Setter
    @Getter
    public static class PaymentRequest {
        private String cardNumber;
        private String expiryDate;
        private String cvc;
        // Pour Les ticketIds
        private String ticketIds;

    }



    @GetMapping("/ticket-payment") // Endpoint pour servir mon template payment.html
    public String showPaiementPage(@RequestParam(value = "ids", required = false) String ticketIds, Model model) {
        model.addAttribute("ticketIds", ticketIds); // Enregistre les IDs des tickets dans le modèle
        // afin de pouvoir servir ma méthode paiement après
        return "payment"; // Retourne le nom du template HTML (payment.html)
    }


}

