package com.infoevent.olympictickets.service;


import com.infoevent.olympictickets.entity.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final TicketService ticketService; // Ajouter le TicketService

    public PaymentService(TicketService ticketService) {
        this.ticketService = ticketService; // Injecter le TicketService
    }

    public PaymentResponse processPayment(String cardNumber, String expiryDate, String cvc, String ticketIds) {
        // Simuler le traitement du paiement
        System.out.println("Traitement du paiement avec les informations suivantes:");
        System.out.println("Numéro de carte: " + cardNumber);
        System.out.println("Date d'expiration: " + expiryDate);
        System.out.println("CVC: " + cvc);
        System.out.println("Processing payment for tickets: " + ticketIds);


        // Simulation de validation de la carte
        if (!isCardValid(cardNumber, expiryDate, cvc)) {
            return new PaymentResponse(false, "Échec du paiement : informations de carte non valides.");
        }

        // Appeler la méthode pour confirmer l'achat des tickets
        for (String ticketId : ticketIds.split(",")) { // Supposons que ticketIds soit une chaîne de caractères séparés par des virgules
            ticketService.confirmTicketPurchase(Long.valueOf(ticketId.trim())); // Appeler confirmTicketPurchase
        }

        // Paiement réussi
        return new PaymentResponse(true, "Paiement réussi");
    }

    // Vérification de la validité de la carte : le nombre de chiffres et son format, la date d'expiration et le crypto
    private boolean isCardValid(String cardNumber, String expiryDate, String cvc) {
        // Logique de validation de la carte
        return cardNumber.length() == 16 && expiryDate.length() == 5 && cvc.length() == 3; // Validation simple
    }

}


