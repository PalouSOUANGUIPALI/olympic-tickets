package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    // Page de gestion des offres
    @GetMapping("/management") // Endpoint pour servir mon template manage-offers.html
    public String showManageOfferPage() {
        return "manage-offers"; // Retourne le nom du template HTML (manage-offers.html)
    }









    /*@GetMapping("/sold-by-type")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATEUR')") // Vérifie que l'utilisateur a le rôle d'administrateur
    @ResponseBody
    public Map<String, List<OfferDto>> getOffersSoldByType() {
        return offerService.getOffersSoldByType(); // Appelle le service pour récupérer les offres vendues par type
    }

     */


    // Méthode principale pour récupérer les offres vendues par type
    @GetMapping(path = "/sold-by-type")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'SCOPE_ADMINISTRATEUR')")
    public String getOffersSoldByType(Model model) {
        return prepareOffersByType(model);
    }


    // Méthode qui prépare les données pour les offres vendues par type
    private String prepareOffersByType(Model model) {
        // Récupérer les offres vendues par type via le service
        Map<String, List<OfferDto>> offersByType = offerService.getOffersSoldByType();

        // S'assurer que chaque type d'offre est bien présent même si aucune offre n'est associée
        offersByType.putIfAbsent("Solo", new ArrayList<>());
        offersByType.putIfAbsent("Duo", new ArrayList<>());
        offersByType.putIfAbsent("Familiale", new ArrayList<>());

        // Ajouter les offres au modèle
        model.addAttribute("offersByType", offersByType);

        return "sold-by-type"; // Retourne le nom du template HTML (sold-by-type.html)
    }











    // Récupération des offres vendues par types
    /*@GetMapping("/sold-by-type")
    public String getOffersSoldByType(Model model) {
        // Récupérer les offres vendues par type via le service
        Map<String, List<OfferDto>> offersByType = offerService.getOffersSoldByType();


        offersByType.putIfAbsent("Solo", new ArrayList<>());
        offersByType.putIfAbsent("Duo", new ArrayList<>());
        offersByType.putIfAbsent("Familiale", new ArrayList<>());

        // Ajouter les offres au modèle
        model.addAttribute("offersByType", offersByType);

        return "sold-by-type"; // Retourne le nom du template HTML (sold-by-type.html)
    }

     */



    // Page de présentation et d'achat de billets
    @GetMapping(path = "/billets") // Endpoint pour servir mon template billets.html
    public String showOfferPage() {
        return "billets"; // Retourne le nom du template HTML (billets.html)
    }

    // Récupérer les offres pour les afficher au frontend de ventes des tickets
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allOffers")
    public ResponseEntity<List<OfferDto>> getAllOffers() {
        List<OfferDto> offers = offerService.getAllOffers();
        return ResponseEntity.ok(offers);
    }

    // Récupérer les offres pour la gestion dans le backoffice ou interface d'administration
    @PreAuthorize("hasRole('ADMINISTRATEUR')") // Vérifie que l'utilisateur a le rôle 'ADMINISTRATEUR'
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allOffers-manage")
    public ResponseEntity<List<OfferDto>> getAllOffersForManagement() {
        List<OfferDto> offers = offerService.getAllOffersForManagment();
        return ResponseEntity.ok(offers);
    }


    // Récupérer les offres par type
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/type/{offerType}")
    public ResponseEntity<List<OfferDto>> getOffersByType(@PathVariable String offerType) {
        List<OfferDto> offers = offerService.getOffersByType(offerType);
        return ResponseEntity.ok(offers);
    }

    // Endpoint de création des offres
    @PostMapping("/create")
    public ResponseEntity<OfferDto> createOffer(@RequestBody OfferDto offerDto) {
        OfferDto responseDto = offerService.createOffer(offerDto);
        return ResponseEntity.ok(responseDto);
    }


    // Endpoint de mise à jour des offres
    @PutMapping("/update/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable Long id, @RequestBody OfferDto offerDto) {
        OfferDto updatedOfferDto = offerService.updateOffer(id, offerDto);
        return ResponseEntity.ok(updatedOfferDto); // Retourner le DTO mis à jour
    }




    // Récupérer une offre par son ID
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("get-offer/{id}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable Long id) {
        OfferDto offer = offerService.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    // Supprimer une offre par son ID
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.ok("L'offre a été supprimée avec succès.");
    }


}
