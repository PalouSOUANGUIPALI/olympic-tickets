package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.entity.*;
import com.infoevent.olympictickets.repository.OfferRepository;
import com.infoevent.olympictickets.repository.TicketRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class OfferService {

    OfferRepository offerRepository;

    TicketRepository ticketRepository;


    // Méthode pour récupérer toutes les offres
    @Transactional(readOnly = true)
    public List<OfferDto> getAllOffers() {
        List<Offer> offers = (List<Offer>) offerRepository.findAll(); // Récupérer toutes les offres

        // Convertir chaque offre en OfferDto
        return offers.stream()
                .map(this::convertToDto) // Utiliser la méthode de conversion définie
                .collect(Collectors.toList());
    }

    // Obtenir toutes les offres pour les gérer dans le backoffice ou interfacde d'administration
    public List<OfferDto> getAllOffersForManagment() {

        // Vérifiez si l'utilisateur est un administrateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMINISTRATEUR"));

        if (!isAdmin) {
            throw new AccessDeniedException("Vous n'avez pas la permission d'accéder à cette ressource");
        }

        List<Offer> offers = (List<Offer>) offerRepository.findAll(); // Récupérer toutes les offres

        // Convertir chaque offre en OfferDto
        return offers.stream()
                .map(this::convertToDto) // Utiliser la méthode de conversion définie
                .collect(Collectors.toList());
    }

    // Méthode pour convertir Offer en OfferDto (comme défini précédemment)
    public OfferDto convertToDto(Offer offer) {
        OfferDto offerDto = new OfferDto();
        offerDto.setId(offer.getId());
        offerDto.setName(offer.getName());
        offerDto.setDescription(offer.getDescription());
        offerDto.setPrice(offer.getPrice());
        offerDto.setCapacity(offer.getCapacity());
        offerDto.setOfferType(offer.getOfferType());

        // définition du type d'offre et les activités incluses dans le DTO pour chacune des offres en particulier
        if (offer instanceof SoloOffer) {
            offerDto.setOfferType("Solo");
            offerDto.setIncludedActivitiesOffer(((SoloOffer) offer).getIncludedActivitiesOffer());
        } else if (offer instanceof DuoOffer) {
            offerDto.setOfferType("Duo");
            offerDto.setIncludedActivitiesOffer(((DuoOffer) offer).getIncludedActivitiesOffer());
        } else if (offer instanceof FamilyOffer) {
            offerDto.setOfferType("Familiale");
            offerDto.setIncludedActivitiesOffer(((FamilyOffer) offer).getIncludedActivitiesOffer());
        }

        return offerDto;
    }

    // Récupération des offres vendues par types : donc des offres associées à des tickets
    @Transactional(readOnly = true)
    public Map<String, List<OfferDto>> getOffersSoldByType() {
        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll(); // Récupérer tous les tickets

        // Regrouper les offres par type
        return tickets.stream()
                .map(ticket -> ticket.getOffer()) // Récupérer l'offre associée au ticket
                .map(this::convertToDto) // Convertir chaque offre en OfferDto
                .collect(Collectors.groupingBy(OfferDto::getOfferType)); // Grouper par type d'offre
    }


    // Création des offres
    @Transactional
    public OfferDto createOffer(OfferDto offerDto) {
        // Vérifier si l'offre existe déjà
        if (offerRepository.existsByName(offerDto.getName())) {
            throw new RuntimeException("Une offre avec ce nom existe déjà.");
        }

        Offer offer;

        // Créer une instance de l'offre selon le type
        switch (offerDto.getOfferType()) {
            case "Solo":
                offer = new SoloOffer();
                break;
            case "Duo":
                offer = new DuoOffer();
                break;
            case "Familiale":
                offer = new FamilyOffer();
                break;
            default:
                throw new RuntimeException("Type d'offre non valide");
        }


        // Remplir les détails de l'offre commune
        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setPrice(offerDto.getPrice());
        offer.setCapacity(offerDto.getCapacity());
        // Définition le type d'offre
        offer.setOfferType(offerDto.getOfferType());

        // Initialiser la date de création
        offer.setCreatedAt(LocalDateTime.now());

        // Remplir les détails spécifiques selon le type d'offre
        if (offer instanceof SoloOffer) {
            ((SoloOffer) offer).setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        } else if (offer instanceof DuoOffer) {
            ((DuoOffer) offer).setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        } else if (offer instanceof FamilyOffer) {
            ((FamilyOffer) offer).setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        }

        // Enregistrer l'offre dans la base de données
        Offer savedOffer = offerRepository.save(offer);

        // Convertir l'offre sauvegardée en DTO
        return convertToDto(savedOffer);
    }




    @Transactional
    public OfferDto updateOffer(Long id, OfferDto offerDto) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        // Mettre à jour les attributs de l'offre
        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setPrice(offerDto.getPrice());
        offer.setCapacity(offerDto.getCapacity());

        // Logique pour les activités incluses selon le type d'offre
        if (offer instanceof SoloOffer) {
            SoloOffer soloOffer = (SoloOffer) offer;
            soloOffer.setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        } else if (offer instanceof DuoOffer) {
            DuoOffer duoOffer = (DuoOffer) offer;
            duoOffer.setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        } else if (offer instanceof FamilyOffer) {
            FamilyOffer familyOffer = (FamilyOffer) offer;
            familyOffer.setIncludedActivitiesOffer(offerDto.getIncludedActivitiesOffer());
        }

        offerRepository.save(offer); // Sauvegarder l'offre mise à jour
        //return new OfferDto(offer); // Retourner le DTO de l'offre mise à jour
        return convertToDto(offer);
    }


    // Méthode pour récupérer les offres par type
    @Transactional(readOnly = true)
    public List<OfferDto> getOffersByType(String offerType) {
        List<Offer> offers;

        switch (offerType.toLowerCase()) {
            case "Solo":
                offers = offerRepository.findAllByOfferType("Solo");
                break;
            case "Duo":
                offers = offerRepository.findAllByOfferType("Duo");
                break;
            case "Familiale":
                offers = offerRepository.findAllByOfferType("Family");
                break;
            default:
                throw new RuntimeException("Type d'offre non valide");
        }

        return offers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Récupérer une offre par son ID
    @Transactional(readOnly = true)
    public OfferDto getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));
        return convertToDto(offer);
    }

    // Méthode pour supprimer une offre
    @Transactional
    public void deleteOffer(Long id) {
        // Trouver tous les tickets associés à l'offre
        List<Ticket> associatedTickets = ticketRepository.findByOfferId(id);

        // Supprimer tous les tickets associés
        // Supprimer le ticket
        ticketRepository.deleteAll(associatedTickets);

        offerRepository.deleteById(id);
    }

}

