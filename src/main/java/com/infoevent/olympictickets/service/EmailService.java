package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.entity.Ticket;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class EmailService {

    private JavaMailSender mailSender;

    private TicketRepository ticketRepository;

    /////////////////////  Envoi du code de validation /////////////////////////

    public void sendValidationEmail(Validation validation) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply.olympic.tickets@gmail.com");
            helper.setTo(validation.getUser().getEmail());
            helper.setSubject("Validation de votre inscription");

            String body = "<p>Bonjour " + "<strong>" + validation.getUser().getFirstName() + "</strong>" + ",</p>" +
                    "<p>Merci de vous être inscrit ! Voici votre code de validation de votre compte :</p>" +
                    "<p><strong>" + validation.getCode() + "</strong></p>" +
                    "<p><strong> Attention :</strong> cellui-ci n'est valide que 5 minutes</p>"
                    ;

            helper.setText(body, true); // le true indique le text HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();

            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage(), e);
        } catch (MailException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de mail : " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer d'autres exceptions potentielles
            throw new RuntimeException("Erreur inattendue lors de l'envoi de l'e-mail : " + e.getMessage(), e);
        }
    }


    // Méthode pour envoyer le mail après achat réussi du ticket
    public void sendTicketPurchaseConfirmation(String email, String offerType, BigDecimal offerPrice, String qrCodePath) {
        try {
            // Créer un objet MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // le true c'est pour activer les pièces jointes

            helper.setFrom("noreply.olympic.tickets@gmail.com");
            helper.setTo(email);
            helper.setSubject("Confirmation d'achat de billet");

            // Créer le contenu du message
            String messageContent = "<h1>Merci pour votre achat !</h1>" +
                    "<p><strong>Voici les détails de votre achat : </strong></p>" + // Type d'offre
                    "<p><strong>Type d'offre : <strong>" + offerType + "</strong></p>" + // Type d'offre
                    "<p><strong>Prix de l'offre : " + offerPrice + " €</strong></p>" + // Prix de l'offre
                    "<p>Veuillez trouver ci-joint votre QR code pour valider votre ticket le jour de l'événement.</p>";

            helper.setText(messageContent, true); // true pour le contenu HTML

            // Ajouter le QR code en pièce jointe image
            helper.addAttachment("qr_code.png", new File(qrCodePath));

            // Envoyer l'e-mail
            mailSender.send(message);
            System.out.println("Email envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception
        }
    }



    /////////////////////  Envoi de mail de rappel  /////////////////////////

    // Rappel de l'événement aux clients réservés et ayant payés de billets

    //@Scheduled(cron = "0 0/5 * * * ?") // Exécute toutes les 5 minutes (pour un essai, vous pouvez activer ce cron)
    // Exécute tous les lundis à 10h00
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyEventReminders() {
        // Récupérer tous les tickets
        List<Ticket> allTickets = (List<Ticket>) ticketRepository.findAll();

        for (Ticket ticket : allTickets) {
            User user = ticket.getUser(); // Récupérer l'utilisateur associé au ticket
            String offerType = ticket.getOfferType();
            BigDecimal offerPrice = ticket.getOffer().getPrice();
            String qrCodePath = ticket.getQrCode();

            // Vérification si l'utilisateur et les données du ticket ne sont pas null
            if (user != null && offerType != null && offerPrice != null && qrCodePath != null) {
                // Envoyer le rappel
                sendEventReminderEmail(user.getEmail(), offerType, offerPrice, qrCodePath);
            } else {
                System.err.println("Une des valeurs est nulle pour le ticket ID: " + ticket.getId());
            }
        }
    }

    private void sendEventReminderEmail(String email, String offerType, BigDecimal offerPrice, String qrCodePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true pour activer les pièces jointes

            helper.setFrom("noreply.olympic.tickets@gmail.com");
            helper.setTo(email);
            helper.setSubject("Rappel de votre événement");

            // Le contenu du message avec mise en forme HTML
            String messageContent = "<h1>Rappel de votre événement à venir !</h1>" +
                    "<p<strong>>Bonjour, veuillez touver ci-joint les informations liées à votre réservation </strong></p>" +
                    "<p>Type d'offre : <strong>" + offerType + "</strong></p>" +
                    "<p>Prix de l'offre : <strong>" + offerPrice + " €</strong></p>" +
                    "<p>Veuillez trouver ci-joint votre QR code pour l'événement.</p>";

            helper.setText(messageContent, true); // true pour le contenu HTML

            // Ajouter le QR code en pièce jointe image
            helper.addAttachment("qr_code.png", new File(qrCodePath));

            // Envoyer l'e-mail
            mailSender.send(message);
            System.out.println("E-mail de rappel envoyé avec succès à " + email + " !");
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception
        }
    }

}
