package com.infoevent.olympictickets.service;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.infoevent.olympictickets.dto.TicketDto;
import com.infoevent.olympictickets.entity.*;
import com.infoevent.olympictickets.exception.TicketNotFoundException;
import com.infoevent.olympictickets.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;


@Slf4j
@AllArgsConstructor
@Service
public class TicketService {

    OfferRepository offerRepository;

    EmailService emailService;

    private TicketRepository ticketRepository;

    private UserRepository userRepository;

    public TicketDto reserveTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();

        // Récupérer l'utilisateur
        User user = userRepository.findById(ticketDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        ticket.setUser(user);

        // Récupérer l'offre selon l'ID fourni dans le DTO
        Offer offer = offerRepository.findById(ticketDto.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        // Assigner l'offre au ticket
        ticket.setOffer(offer);

        // Définir le type d'offre pour le ticket
        if (offer instanceof SoloOffer) {
            ticket.setOfferType("Solo");
        } else if (offer instanceof DuoOffer) {
            ticket.setOfferType("Duo");
        } else if (offer instanceof FamilyOffer) {
            ticket.setOfferType("Family");
        } else {
            throw new RuntimeException("Type d'offre inconnu");
        }

        // Génération de la clé de sécurité
        String secondSecurityKey = generateSecurityKey();
        log.info("deuxième clé de sécurisation du billet: {}", secondSecurityKey);

        // Générer le QR code
        // A noter que pour la réservation d'un administrateur, son qr code sera encoder sans un
        // SecurityKey() mais pour les utilisateur lambda, j'ai ajouté le SecurityKey et secondSecurityKey
        // dans l'encodage de leur qr code en plus de leur id et de l'id de l'offre
        String qrCodeData = "TicketID:" + ticket.getId() +
                "offerId:" + ticket.getOffer().getId() +
                "security_key_for_ticket:"
                + user.getSecurityKey() + "second_Security_Key" + secondSecurityKey;
        byte[] qrCodeBytes = generateQrCode(qrCodeData);

        // Enregistrer l'image QR code dans le dossier " src/main/resources/qr_codes/ " de mon projet
        String qrCodePath = "src/main/resources/qr_codes/ticket_" + ticket.getId() + ".png"; // Spécification du répertoire ici
        try (FileOutputStream fos = new FileOutputStream(qrCodePath)) {
            fos.write(qrCodeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ticket.setQrCode(qrCodePath); // Stocker le chemin du QR code

        ticketDto.setCreatedAt(LocalDateTime.now()); // Définir la date de création

        // Enregistrer le ticket dans la base de données
        Ticket savedTicket = ticketRepository.save(ticket);

        // Convertir le ticket sauvegardé en DTO pour la réponse
        return convertToDto(savedTicket);
    }

    // La deuxième clé d'encodage du qr code : clé générée lors de la réservation/achat du billet
    public String generateSecurityKey() {
        // Utilisation de SecureRandom pour générer une clé sécurisée
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24]; // Taille de la clé en octets
        secureRandom.nextBytes(randomBytes);

        // Conversion en chaîne de caractères
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }



    // Méthode pour convertir Ticket en TicketDto
    private TicketDto convertToDto(Ticket ticket) {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        ticketDto.setQrCode(ticket.getQrCode());
        ticketDto.setUserId(ticket.getUser().getId());
        ticketDto.setOfferId(ticket.getOffer().getId()); // Récupérer l'ID de l'offre
        ticketDto.setCreatedAt(ticket.getCreatedAt());
        ticketDto.setOfferType(ticket.getOfferType());



        return ticketDto;
    }

    /////////////////////  Génération du Qr Code /////////////////////////

    public byte[] generateQrCode(String qrCodeData) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray(); // Retourner les données de l'image QR code
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //////////////////////////////   Conirmation du billet acheté par mail /////////////////////////////////

    public void confirmTicketPurchase(Long ticketId) {
        // Récupérer le ticket par ID
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Billet non trouvé"));

        // Récupérer l'utilisateur associé au ticket
        User user = ticket.getUser();
        if (user == null) {
            throw new RuntimeException("Utilisateur non associé à ce ticket.");
        }

        // Préparer les détails du ticket pour l'e-mail de confirmation après achat
        String offerType = ticket.getOfferType(); // Type d'offre
        BigDecimal offerPrice = ticket.getOffer().getPrice(); // Prix de l'offre

        // Envoyer la confirmation par e-mail
        emailService.sendTicketPurchaseConfirmation(user.getEmail(), offerType, offerPrice, ticket.getQrCode());
    }

    ///////////////////////////////////////////////////
    public List<Ticket> getAllTickets() {
        return (List<Ticket>) ticketRepository.findAll(); // Récupérer tous les tickets
    }

}