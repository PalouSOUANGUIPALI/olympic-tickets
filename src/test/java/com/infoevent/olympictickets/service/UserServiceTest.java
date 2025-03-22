package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.UserDto;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.exception.UserNotFoundException;
import com.infoevent.olympictickets.repository.JwtRepository;
import com.infoevent.olympictickets.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private ValidationService validationService;
    @Mock private JwtRepository jwtRepository;

    @InjectMocks private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("INITIALISATION : Mocking et injection des dépendances terminé.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("TEARDOWN : Nettoyage des mocks terminé.\n");
    }

    @Test
    void testInscription_success() {
        System.out.println("TEST : testInscription_success démarré");
        // Étape 1 : Préparer les données d'entrée
        UserDto dto = new UserDto();
        dto.setFirstName("Jean");
        dto.setLastName("Dupont");
        dto.setEmail("jean@test.com");
        dto.setPassword("1234");

        // Étape 2 : Définir le comportement attendu des mocks
        when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hashed");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setFirstName("Jean");
        savedUser.setLastName("Dupont");
        savedUser.setEmail("jean@test.com");
        savedUser.setSecurityKey("securekey");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Étape 3 : Appeler la méthode à tester
        UserDto result = userService.inscription(dto);

        // Étape 4 : Vérifier les résultats
        assertEquals("Jean", result.getFirstName());
        assertEquals("securekey", result.getSecurityKey());
        System.out.println("RÉSULTAT : testInscription_success est passé avec succès.");
    }

    @Test
    void testGenerateSecurityKey() {
        System.out.println("TEST : testGenerateSecurityKey est démarré");
        // Étape 1 : Appeler la méthode à tester
        String key = userService.generateSecurityKey();

        // Étape 2 : Vérifier que la clé est générée et a une longueur suffisante
        assertNotNull(key);
        assertTrue(key.length() >= 30);
        System.out.println("RÉSULTAT : testGenerateSecurityKey est passé avec succès.");
    }

    @Test
    void testActivation_codeValid() {
        System.out.println("TEST : testActivation_codeValid est démarré");
        // Étape 1 : Préparer les données simulées
        Validation validation = new Validation();
        validation.setId(1);
        validation.setExpirationTime(Instant.now().plusSeconds(300));
        User user = new User();
        user.setId(1);
        validation.setUser(user);

        // Étape 2 : Définir le comportement attendu des mocks
        when(validationService.userActivationCodeVerification("123456")).thenReturn(validation);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Étape 3 : Appeler la méthode à tester
        userService.activation(Map.of("code", "123456"));

        // Étape 4 : Vérifier les appels attendus
        verify(userRepository).save(user);
        verify(validationService).deleteValidation(1);
        System.out.println("RÉSULTAT : testActivation_codeValid est passé avec succès.");
    }

    @Test
    void testActivation_codeExpired() {
        System.out.println("TEST : testActivation_codeExpired est démarré");
        // Étape 1 : Créer une validation expirée
        Validation validation = new Validation();
        validation.setExpirationTime(Instant.now().minusSeconds(10));
        validation.setUser(new User());

        // Étape 2 : Définir le comportement attendu
        when(validationService.userActivationCodeVerification("code")).thenReturn(validation);

        // Étape 3 : Vérifier que l'exception est levée
        assertThrows(RuntimeException.class, () -> userService.activation(Map.of("code", "code")));
        System.out.println("RÉSULTAT : testActivation_codeExpired est passé avec succès.");
    }

    @Test
    void testPasswordChanging_appelleValidationService() {
        System.out.println("TEST : testPasswordChanging_appelleValidationService est démarré");
        // Étape 1 : Préparer l'utilisateur
        User user = new User();
        user.setEmail("mail@test.com");
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(user));

        // Étape 2 : Appeler la méthode à tester
        userService.passwordChanging(Map.of("email", "mail@test.com"));

        // Étape 3 : Vérifier que le service de validation est appelé
        verify(validationService).saveUserInValidationService(user);
        System.out.println("RÉSULTAT : testPasswordChanging_appelleValidationService est passé avec succès.");
    }

    @Test
    void testMakeNewPassword_success() {
        System.out.println("TEST : testMakeNewPassword_success est démarré");
        // Étape 1 : Préparer les données
        User user = new User();
        user.setEmail("user@x.com");
        Validation validation = new Validation();
        validation.setUser(user);

        // Étape 2 : Configurer les comportements des mocks
        when(userRepository.findByEmail("user@x.com")).thenReturn(Optional.of(user));
        when(validationService.userActivationCodeVerification("111111")).thenReturn(validation);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");

        // Étape 3 : Appeler la méthode à tester
        userService.makeNewPassword(Map.of("email", "user@x.com", "code", "111111", "password", "newPass"));

        // Étape 4 : Vérifier que l'utilisateur a été sauvegardé
        verify(userRepository).save(user);
        System.out.println("RÉSULTAT : testMakeNewPassword_success est passé avec succès.");
    }

    @Test
    void testGetAllUsers() {
        System.out.println("TEST : testGetAllUsers est démarré");
        // Étape 1 : Simuler des utilisateurs existants
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        // Étape 2 : Appeler la méthode
        List<User> result = userService.getAllUsers();

        // Étape 3 : Vérifier la taille de la liste retournée
        assertEquals(2, result.size());
        System.out.println("RÉSULTAT : testGetAllUser est passé avec succès.");
    }

    @Test
    void testGetUsersByNameOrFirstNameContaining() {
        System.out.println("TEST : testGetUsersByNameOrFirstNameContaining est démarré");
        // Étape 1 : Préparer une liste simulée
        List<User> users = List.of(new User(), new User());
        when(userRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase("a", "a")).thenReturn(users);

        // Étape 2 : Appeler la méthode
        List<User> result = userService.getUsersByNameOrFirstNameContaining("a");

        // Étape 3 : Vérifier les résultats
        assertEquals(2, result.size());
        System.out.println("RÉSULTAT : testGetUsersByNameOrFirstNameContaining est passé avec succès.");
    }

    @Test
    void testUpdateUser_success() {
        System.out.println("TEST : testUpdateUser_success est démarré");
        // Étape 1 : Créer un utilisateur simulé et DTO d'entrée
        User user = new User();
        user.setId(1);
        UserDto dto = new UserDto();
        dto.setFirstName("New");
        dto.setLastName("Name");
        dto.setEmail("email@test.com");
        dto.setPassword("pass");

        // Étape 2 : Définir les comportements des mocks
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);

        // Étape 3 : Appeler la méthode
        User result = userService.updateUser(1, dto);

        // Étape 4 : Vérifier la mise à jour
        assertEquals("email@test.com", result.getEmail());
        System.out.println("RÉSULTAT : testUpdateUser_success est passé avec succès.");
    }

    @Test
    void testDeleteUser_success() {
        System.out.println("TEST : testDeleteUser_success est démarré");
        // Étape 1 : Définir les mocks
        doNothing().when(validationService).updateUserIdToNull(1);
        doNothing().when(jwtRepository).updateUserIdToNull(1);
        doNothing().when(userRepository).deleteById(1);

        // Étape 2 : Appeler la méthode
        userService.deleteUser(1);

        // Étape 3 : Vérifier les appels
        verify(validationService).updateUserIdToNull(1);
        verify(jwtRepository).updateUserIdToNull(1);
        verify(userRepository).deleteById(1);
        System.out.println("RÉSULTAT : testDeleteUser_success est passé avec succès.");
    }

    @Test
    void testLoadUserByUsername_success() {
        System.out.println("TEST : testLoadUserByUsername_success est démarré");
        // Étape 1 : Préparer un utilisateur simulé
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Étape 2 : Appeler la méthode
        User result = userService.loadUserByUsername("test@example.com");

        // Étape 3 : Vérifier le résultat
        assertEquals("test@example.com", result.getEmail());
        System.out.println("RÉSULTAT : testLoadUserByUsername_success est passé avec succès.");
    }

    @Test
    void testLoadUserByUsername_notFound() {
        System.out.println("TEST : testLoadUserByUsername_notFound est démarré");
        // Étape 1 : Simuler l'absence d'utilisateur
        when(userRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

        // Étape 2 : Vérifier l'exception levée
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("inconnu@example.com"));
        System.out.println("RÉSULTAT : testLoadUserByUsername_notFound est passé avec succès.");
    }

    @Test
    void testDeleteUserByEmail_success() {
        System.out.println("TEST : testDeleteUserByEmail_success est démarré");
        // Étape 1 : Préparer un utilisateur simulé
        User user = new User();
        user.setId(42);
        user.setEmail("delete@user.com");
        when(userRepository.findByEmail("delete@user.com")).thenReturn(Optional.of(user));

        // Étape 2 : Appeler la méthode
        userService.deleteUserByEmail("delete@user.com");

        // Étape 3 : Vérifier les appels de suppression
        verify(jwtRepository).updateUserIdToNull(42);
        verify(userRepository).delete(user);
        System.out.println("RÉSULTAT : testDeleteUserByEmail_success est passé avec succès.");
    }

    @Test
    void testDeleteUserByEmail_userNotFound() {
        System.out.println("TEST : testDeleteUserByEmail_userNotFound est démarré");
        // Étape 1 : Simuler un utilisateur introuvable
        when(userRepository.findByEmail("introuvable@.com")).thenReturn(Optional.empty());

        // Étape 2 : Vérifier l'exception levée
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByEmail("introuvable@.com"));
        System.out.println("RÉSULTAT : testDeleteUserByEmail_userNotFound est passé avec succès.");
    }

    @Test
    void testGetUserById_success() {
        System.out.println("TEST : testGetUserById_success est démarré");
        // Étape 1 : Préparer l'utilisateur simulé
        User user = new User();
        user.setId(1);
        user.setFirstName("Alice");
        user.setLastName("Martin");
        user.setEmail("alice@test.com");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Étape 2 : Appeler la méthode
        UserDto result = userService.getUserById(1);

        // Étape 3 : Vérifier le contenu du DTO retourné
        assertEquals(1, result.getId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Martin", result.getLastName());
        assertEquals("alice@test.com", result.getEmail());
        System.out.println("RÉSULTAT : testGetUserById_success est passé avec succès.");
    }
}