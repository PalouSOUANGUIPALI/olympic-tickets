package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.OfferDto;
import com.infoevent.olympictickets.entity.*;
import com.infoevent.olympictickets.repository.OfferRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;

class OfferServiceTest {

    @Mock private OfferRepository offerRepository;

    @InjectMocks private OfferService offerService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : OfferService et dépendances mockées.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Nettoyage est test terminés avec succès.\n");
    }

    @Test
    void testGetOffersByType_validType() {
        // Étape 1 : Préparer une offre fictive Solo
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Offre Solo");
        offer.setOfferType("Solo");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(1);
        ((SoloOffer) offer).setIncludedActivitiesOffer("Visite guidée dans la ville de Paris");

        List<Offer> offers = List.of(offer);

        // Étape 2 : Simuler le comportement du repository
        when(offerRepository.findAllByOfferType("Solo")).thenReturn(offers);

        // Étape 3 : Appeler la méthode du service
        List<OfferDto> result = offerService.getOffersByType("solo");

        // Étape 4 : Vérification
        assertEquals(1, result.size());
        assertEquals("Offre Solo", result.get(0).getName());
        assertEquals("Solo", result.get(0).getOfferType());
        System.out.println("RÉSULTAT : testGetOffersByType_validType (solo) est passé avec succès.");
    }

    @Test
    void testGetOffersByType_duo() {
        // Étape 1 : Préparer une offre Duo
        Offer offer = new DuoOffer();
        offer.setId(2L);
        offer.setName("Offre Duo");
        offer.setOfferType("Duo");
        offer.setPrice(BigDecimal.valueOf(150.0));
        offer.setCapacity(2);
        ((DuoOffer) offer).setIncludedActivitiesOffer("Musée + Dîner dans la soirée");

        List<Offer> offers = List.of(offer);

        // Étape 2 : Simuler la méthode findAllByOfferType pour l'offre "Duo"
        when(offerRepository.findAllByOfferType("Duo")).thenReturn(offers);

        // Étape 3 : Appeler le service avec le type
        List<OfferDto> result = offerService.getOffersByType("duo");

        // Étape 4 : Vérification
        assertEquals(1, result.size());
        assertEquals("Offre Duo", result.get(0).getName());
        assertEquals("Duo", result.get(0).getOfferType());
        System.out.println("RÉSULTAT : testGetOffersByType_duo est passé avec succès.");
    }

    @Test
    void testGetOffersByType_familiale() {
        // Étape 1 : Préparer une offre Familiale
        Offer offer = new FamilyOffer();
        offer.setId(3L);
        offer.setName("Offre Famille");
        offer.setOfferType("Family");
        offer.setPrice(BigDecimal.valueOf(250.0));
        offer.setCapacity(4);
        ((FamilyOffer) offer).setIncludedActivitiesOffer("Parc + Zoo + Musée");

        List<Offer> offers = List.of(offer);

        // Étape 2 : Simuler la méthode findAllByOfferType pour "Family"
        when(offerRepository.findAllByOfferType("Family")).thenReturn(offers);

        // Étape 3 : Appeler le service avec le type en minuscule
        List<OfferDto> result = offerService.getOffersByType("familiale");

        // Étape 4 : Vérification
        assertEquals(1, result.size());
        assertEquals("Offre Famille", result.get(0).getName());
        assertEquals("Familiale", result.get(0).getOfferType());
        System.out.println("RÉSULTAT : testGetOffersByType_familiale est passé avec succès.");
    }

    @Test
    void testGetAllOffers() {
        // Étape 1 : Préparer une liste d'offres (exemple : une offre Solo)
        SoloOffer offer = new SoloOffer();
        offer.setId(10L);
        offer.setName("Solo 2024");
        offer.setOfferType("Solo");
        offer.setPrice(BigDecimal.valueOf(120.0));
        offer.setCapacity(1);
        offer.setIncludedActivitiesOffer("Visite guidée du musée du Louvre");

        when(offerRepository.findAll()).thenReturn(List.of(offer));

        // Étape 2 : Appeler la méthode du service
        List<OfferDto> result = offerService.getAllOffers();

        // Étape 3 : Vérification des valeurs retournées
        assertEquals(1, result.size());
        assertEquals("Solo 2024", result.get(0).getName());
        System.out.println("RÉSULTAT : testGetAllOffers est passé avec succès.");
    }

    @Test
    void testGetOfferById_found() {
        // Étape 1 : Préparer une offre existante
        SoloOffer offer = new SoloOffer();
        offer.setId(20L);
        offer.setName("Solo Offer Found");
        offer.setOfferType("Solo");
        offer.setIncludedActivitiesOffer("Concert");

        when(offerRepository.findById(20L)).thenReturn(Optional.of(offer));

        // Étape 2 : Appeler la méthode du service
        OfferDto dto = offerService.getOfferById(20L);

        // Étape 3 : Vérification
        assertEquals("Solo Offer Found", dto.getName());
        assertEquals("Solo", dto.getOfferType());
        System.out.println("RÉSULTAT : testGetOfferById_found est passé avec succès : " + dto.getName());
    }

    @Test
    void testGetOfferById_notFound() {
        // Étape 1 : Simuler une absence d'offre
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        // Étape 2 : Vérifier que l'exception est levée
        Exception exception = assertThrows(RuntimeException.class, () -> offerService.getOfferById(99L));
        assertEquals("Offre non trouvée", exception.getMessage());
        System.out.println("RÉSULTAT : testGetOfferById_notFound a bien levé l'exception attendue : "
                + exception.getMessage());
    }

    @Test
    void testGetAllOffersForManagement_adminAccess() {
        // Étape 1 : Préparer les mocks pour le contexte de sécurité
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        GrantedAuthority adminAuthority = mock(GrantedAuthority.class);

        // Étape 2 : Simuler le contexte admin avec une liste mutable
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(adminAuthority);
        when(adminAuthority.getAuthority()).thenReturn("ROLE_ADMINISTRATEUR");
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Étape 3 : Préparer une liste d'offres simulées
        Offer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Test Offer");
        offer.setOfferType("Solo");
        ((SoloOffer) offer).setIncludedActivitiesOffer("Balade au bord de la seyne");
        when(offerRepository.findAll()).thenReturn(List.of(offer));

        // Étape 4 : Appeler la méthode à tester
        List<OfferDto> result = offerService.getAllOffersForManagment();

        // Étape 5 : Vérification
        assertEquals(1, result.size());
        assertEquals("Test Offer", result.get(0).getName());
        assertEquals("Solo", result.get(0).getOfferType());
        System.out.println("RÉSULTAT : testGetAllOffersForManagement_adminAccess est passé avec succès.");
    }

    @Test
    void testGetAllOffersForManagement_nonAdminAccess() {
        // Étape 1 : Préparer les mocks pour le contexte de sécurité
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        GrantedAuthority userAuthority = mock(GrantedAuthority.class);

        // Étape 2 : Simuler un utilisateur non-admin avec une liste mutable
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(userAuthority);
        when(userAuthority.getAuthority()).thenReturn("ROLE_UTILISATEUR");
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Étape 3 : Appeler la méthode et vérifier que l'exception est levée
        Exception exception = assertThrows(AccessDeniedException.class, () -> offerService.getAllOffersForManagment());
        assertEquals("Vous n'avez pas la permission d'accéder à cette ressource", exception.getMessage());
        System.out.println("RÉSULTAT : testGetAllOffersForManagement_nonAdminAccess est passé avec succès. " +
                exception.getMessage());
    }

    @Test
    void testConvertToDto_soloOffer() {
        // Étape 1 : Créer une instance SoloOffer
        SoloOffer offer = new SoloOffer();
        offer.setId(1L);
        offer.setName("Solo Paris");
        offer.setDescription("Visite de Paris");
        offer.setPrice(BigDecimal.valueOf(100.0));
        offer.setCapacity(1);
        offer.setOfferType("Solo");
        offer.setIncludedActivitiesOffer("Tour Eiffel, Louvre");

        // Étape 2 : Appeler la méthode de conversion du service
        OfferDto dto = offerService.convertToDto(offer);

        // Étape 3 : Vérifier le contenu du DTO
        assertEquals(1L, dto.getId());
        assertEquals("Solo Paris", dto.getName());
        assertEquals("Visite de Paris", dto.getDescription());
        assertEquals(new BigDecimal("100.0"), dto.getPrice());
        assertEquals(1, dto.getCapacity());
        assertEquals("Solo", dto.getOfferType());
        assertEquals("Tour Eiffel, Louvre", dto.getIncludedActivitiesOffer());
        System.out.println("RÉSULTAT : testConvertToDto_soloOffer est passé avec succès.");
    }

    @Test
    void testConvertToDto_duoOffer() {
        // Étape 1 : Créer une instance DuoOffer
        DuoOffer offer = new DuoOffer();
        offer.setId(2L);
        offer.setName("Duo Lyon");
        offer.setDescription("Visite gastronomique");
        offer.setPrice(BigDecimal.valueOf(180.0));
        offer.setCapacity(2);
        offer.setOfferType("Duo");
        offer.setIncludedActivitiesOffer("Dîner + Musée");

        // Étape 2 : Appeler la méthode de conversion du service
        OfferDto dto = offerService.convertToDto(offer);

        // Étape 3 : Vérifier le contenu du DTO
        assertEquals("Duo", dto.getOfferType());
        assertEquals("Dîner + Musée", dto.getIncludedActivitiesOffer());
        System.out.println("RÉSULTAT : testConvertToDto_duoOffer est passé avec succès.");
    }

    @Test
    void testConvertToDto_familyOffer() {
        // Étape 1 : Créer une instance FamilyOffer
        FamilyOffer offer = new FamilyOffer();
        offer.setId(3L);
        offer.setName("Offre Famille");
        offer.setDescription("Parc + Zoo");
        offer.setPrice(BigDecimal.valueOf(250.0));
        offer.setCapacity(4);
        offer.setOfferType("Familiale");
        offer.setIncludedActivitiesOffer("Zoo, Jardin, Musée");

        // Étape 2 : Appeler la méthode de conversion du service
        OfferDto dto = offerService.convertToDto(offer);

        // Étape 3 : Vérifier le contenu du DTO
        assertEquals("Familiale", dto.getOfferType());
        assertEquals("Zoo, Jardin, Musée", dto.getIncludedActivitiesOffer());
        System.out.println("RÉSULTAT : testConvertToDto_familyOffer est passé avec succès.\n");
        System.out.println("L'offre " + offer.getName() + " est convertie avec succès en dto " + dto.getName());
    }
}
