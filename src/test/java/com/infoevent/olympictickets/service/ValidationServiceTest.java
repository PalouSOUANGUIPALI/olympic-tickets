package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.repository.ValidationRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationServiceTest {

    @Mock
    private ValidationRepository validationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ValidationService validationService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : ValidationService et dépendances mockées.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Nettoyage terminé avec succès.\n");
    }

    @Test
    void testSaveUserInValidationService() {
        // Étape 1 : Créer un utilisateur fictif
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        // Étape 2 : Capturer l'objet Validation sauvegardé
        ArgumentCaptor<Validation> validationCaptor = ArgumentCaptor.forClass(Validation.class);

        // Étape 3 : Appeler la méthode à tester
        validationService.saveUserInValidationService(user);

        // Étape 4 : Vérifier que la méthode save est appelée avec une Validation non nulle
        verify(validationRepository, times(1)).save(validationCaptor.capture());
        verify(emailService, times(1)).sendValidationEmail(any(Validation.class));

        Validation savedValidation = validationCaptor.getValue();
        assertNotNull(savedValidation);
        assertEquals(user, savedValidation.getUser());
        assertNotNull(savedValidation.getCode());
        assertNotNull(savedValidation.getCreatedAt());
        System.out.println("RÉSULTAT : testSaveUserInValidationService est passé avec succès.");
    }

    @Test
    void testUserActivationCodeVerification_validCode() {
        // Étape 1 : Préparer une validation simulée
        Validation validation = new Validation();
        validation.setCode("123456");

        // Étape 2 : Simuler la recherche par code
        when(validationRepository.findByCode("123456")).thenReturn(Optional.of(validation));

        // Étape 3 : Appeler la méthode et vérifier le résultat
        Validation result = validationService.userActivationCodeVerification("123456");
        assertEquals("123456", result.getCode());
        System.out.println("RÉSULTAT : testUserActivationCodeVerification_validCode est passé avec succès.");
    }

    @Test
    void testUserActivationCodeVerification_invalidCode() {
        // Étape 1 : Simuler un code inexistant
        when(validationRepository.findByCode("000000")).thenReturn(Optional.empty());

        // Étape 2 : Vérifier que l'exception est levée
        Exception exception = assertThrows(RuntimeException.class, () -> {
            validationService.userActivationCodeVerification("000000");
        });

        assertEquals("Votre code est invalide", exception.getMessage());
        System.out.println("RÉSULTAT : testUserActivationCodeVerification_invalidCode est passé avec succès.");
    }

    @Test
    void testUpdateUserIdToNull() {
        // Étape 1 : Appeler la méthode de mise à jour
        validationService.updateUserIdToNull(1);

        // Étape 2 : Vérifier l'appel au repository
        verify(validationRepository).updateUserIdToNull(1);
        System.out.println("RÉSULTAT : testUpdateUserIdToNull est passé avec succès.");
    }

    @Test
    void testDeleteValidation() {
        // Étape 1 : Appeler la méthode de suppression
        validationService.deleteValidation(42);

        // Étape 2 : Vérifier que la méthode de suppression est bien appelée
        verify(validationRepository).deleteById(42);
        System.out.println("RÉSULTAT : testDeleteValidation est passé avec succès.");
    }

    @Test
    void testRemoveUselessValidation() {
        // Étape 1 : Appeler la méthode de suppression de masse
        validationService.removeUselessValidation();

        // Étape 2 : Vérifier l'appel au repository
        verify(validationRepository).deleteAll();
        System.out.println("RÉSULTAT : testRemoveUselessValidation est passé avec succès.");
    }
}
