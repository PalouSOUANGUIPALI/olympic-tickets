package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.config.JwtService;
import com.infoevent.olympictickets.dto.AuthentificationDTO;
import com.infoevent.olympictickets.dto.UserDto;
import com.infoevent.olympictickets.entity.UserRole;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.enums.RoleType;
import com.infoevent.olympictickets.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagementControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserService userService;
    @Mock private JwtService jwtService;

    @InjectMocks
    private UserManagementController controller;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks et injection dans le contrôleur
        closeable = MockitoAnnotations.openMocks(this);
        controller.olympicTicketsBaseUrl = "http://localhost:8080";
        System.out.println("INITIALISATION : Mocks et contrôleur initialisés.");
    }

    @AfterEach
    void tearDown() throws Exception {
        // Libération des ressources et nettoyage
        closeable.close();
        System.out.println("TEARDOWN : Test de la méthode terminé avec succès.\n");
    }

    @Test
    void testInscription() {
        System.out.println("TEST : testInscription démarré.");

        // Étape 1 : Préparer un UserDto simulé pour l'inscription
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setSecurityKey("SEC123");
        System.out.println("ÉTAPE 1 : Données utilisateur préparées -> ID: " +
                userDto.getId() + ", SecurityKey: " + userDto.getSecurityKey());

        // Étape 2 : Simuler le retour du service lors de l'inscription
        when(userService.inscription(userDto)).thenReturn(userDto);
        System.out.println("ÉTAPE 2 : Service d'inscription mocké pour retourner les données préparées.");

        // Étape 3 : Appeler la méthode du contrôleur
        ResponseEntity<Map<String, Object>> response = controller.inscription(userDto);
        System.out.println("ÉTAPE 3 : Méthode controller.inscription() appelée.");

        // Étape 4 : Vérifier la réponse retournée par le contrôleur
        System.out.println("ÉTAPE 4 : Vérification des résultats...");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().get("id"));
        assertEquals("SEC123", response.getBody().get("securityKey"));

        System.out.println("RÉSULTAT : testInscription passé avec succès.\n");
    }

    @Test
    void testActivation() {
        System.out.println("TEST : testActivation démarré.");

        // Étape 1 : Préparer les données d'activation (email + code)
        Map<String, String> activationData = Map.of(
                "email", "user@test.com",
                "activationCode", "123456"
        );
        System.out.println("ÉTAPE 1 : Données d'activation préparées -> " + activationData);

        // Étape 2 : Simuler que l'appel au service ne fait rien (void)
        doNothing().when(userService).activation(activationData);
        System.out.println("ÉTAPE 2 : Service d'activation mocké pour ne rien faire.");

        // Étape 3 : Appeler la méthode du contrôleur
        controller.activation(activationData);
        System.out.println("ÉTAPE 3 : Méthode controller.activation() appelée.");

        // Étape 4 : Vérifier que le service a bien été appelé avec les bonnes données
        verify(userService, times(1)).activation(activationData);
        System.out.println("RÉSULTAT : testActivation passé avec succès.\n");
    }

    @Test
    void testConnexion_admin() {
        System.out.println("TEST : testConnexion_admin démarré.");

        // Étape 1 : Préparer un DTO d'authentification et un mock d'authentification
        AuthentificationDTO dto = new AuthentificationDTO("admin@test.com", "pass");
        Authentication auth = mock(Authentication.class);
        System.out.println("ÉTAPE 1 : DTO d'authentification préparé -> " + dto.username());

        // Étape 2 : Simuler l'authentification et les appels de service
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);

        User user = new User();
        user.setId(1);
        user.setEmail("admin@test.com");
        UserRole role = new UserRole();
        role.setLabel(RoleType.ADMINISTRATEUR);
        user.setRole(role);

        when(userService.getUserByEmail("admin@test.com")).thenReturn(user);
        when(jwtService.generate("admin@test.com")).thenReturn(new HashMap<>(Map.of("token", "abc123")));

        System.out.println("ÉTAPE 2 : Authentification simulée, utilisateur mocké avec rôle ADMINISTRATEUR.");

        // Étape 3 : Appeler la méthode à tester
        ResponseEntity<Map<String, String>> response = controller.connexion(dto);
        System.out.println("ÉTAPE 3 : Méthode controller.connexion() appelée.");

        // Étape 4 : Vérification des résultats
        System.out.println("ÉTAPE 4 : Vérification de la réponse...");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("abc123", response.getBody().get("token"));
        assertTrue(response.getBody().get("redirectUrl").contains("/offers/management"));

        System.out.println("RÉSULTAT : testConnexion_admin passé avec succès.\n");
    }

    @Test
    void testConnexion_user() {
        System.out.println("TEST : testConnexion_user démarré.");

        // Étape 1 : Préparer un DTO d'authentification pour un utilisateur standard
        AuthentificationDTO dto = new AuthentificationDTO("user@test.com", "pass");
        Authentication auth = mock(Authentication.class);
        System.out.println("ÉTAPE 1 : DTO d'authentification préparé -> " + dto.username());

        // Étape 2 : Simuler l'authentification réussie et la récupération de l'utilisateur
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);

        User user = new User();
        user.setId(2);
        user.setEmail("user@test.com");
        UserRole role = new UserRole();
        role.setLabel(RoleType.UTILISATEUR);
        user.setRole(role);

        when(userService.getUserByEmail("user@test.com")).thenReturn(user);
        when(jwtService.generate("user@test.com")).thenReturn(new HashMap<>(Map.of("token", "def456")));

        System.out.println("ÉTAPE 2 : Authentification simulée avec un utilisateur au rôle UTILISATEUR.");

        // Étape 3 : Appeler la méthode à tester
        ResponseEntity<Map<String, String>> response = controller.connexion(dto);
        System.out.println("ÉTAPE 3 : Méthode controller.connexion() appelée.");

        // Étape 4 : Vérifier les informations retournées
        System.out.println("ÉTAPE 4 : Vérification des données retournées...");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("def456", response.getBody().get("token"));
        assertTrue(response.getBody().get("redirectUrl").contains("/users/offres"));
        assertEquals("2", response.getBody().get("userId"));

        System.out.println("RÉSULTAT : testConnexion_user passé avec succès.\n");
    }

    @Test
    void testSetNewPassword() {
        System.out.println("TEST : testSetNewPassword démarré.");

        // Étape 1 : Préparation des données d'entrée
        Map<String, String> data = Map.of("token", "abc", "newPassword", "secure");
        System.out.println("ÉTAPE 1 : Données de changement de mot de passe préparées -> " + data);

        // Étape 2 : Simulation du service
        doNothing().when(userService).makeNewPassword(data);
        System.out.println("ÉTAPE 2 : Service userService.makeNewPassword() mocké pour ne rien faire.");

        // Étape 3 : Appel de la méthode du contrôleur
        controller.setNewPassword(data);
        System.out.println("ÉTAPE 3 : Méthode controller.setNewPassword() appelée.");

        // Étape 4 : Vérification que le service a bien été invoqué avec les bonnes données
        verify(userService).makeNewPassword(data);
        System.out.println("RÉSULTAT : testSetNewPassword passé avec succès.\n");
    }

    @Test
    void testRefreshToken() {
        System.out.println("TEST : testRefreshToken démarré.");

        // Étape 1 : Préparer les données d'entrée et la réponse simulée
        Map<String, String> input = Map.of("refreshToken", "tok123");
        Map<String, String> output = Map.of("accessToken", "newJWT");
        System.out.println("ÉTAPE 1 : Données d'entrée préparées -> " + input);

        // Étape 2 : Simuler la génération du token
        when(jwtService.refreshToken(input)).thenReturn(output);
        System.out.println("ÉTAPE 2 : jwtService.refreshToken() simulé pour retourner -> " + output);

        // Étape 3 : Appeler la méthode du contrôleur
        Map<String, String> result = controller.refreshToken(input);
        System.out.println("ÉTAPE 3 : Méthode controller.refreshToken() appelée.");

        // Étape 4 : Vérification du token retourné
        assertEquals("newJWT", result.get("accessToken"));
        System.out.println("RÉSULTAT : testRefreshToken passé avec succès.\n");
    }

    @Test
    void testGetAllUsers() {
        System.out.println("TEST : testGetAllUsers démarré.");

        // Étape 1 : Préparer une liste fictive d'utilisateurs
        List<User> users = List.of(new User(), new User());
        System.out.println("ÉTAPE 1 : Liste d'utilisateurs simulée créée avec " + users.size() + " éléments.");

        // Étape 2 : Simuler le service
        when(userService.getAllUsers()).thenReturn(users);
        System.out.println("ÉTAPE 2 : Simulation du service userService.getAllUsers().");

        // Étape 3 : Appeler la méthode du contrôleur
        List<User> result = controller.getAllUsers();
        System.out.println("ÉTAPE 3 : Méthode controller.getAllUsers() appelée.");

        // Étape 4 : Vérification du nombre d'utilisateurs retournés
        assertEquals(2, result.size());
        System.out.println("RÉSULTAT : testGetAllUsers passé avec succès. Nombre d'utilisateurs retournés : " + result.size() + "\n");
    }

    @Test
    void testGetUser_found() {
        System.out.println("TEST : testGetUser_found démarré.");

        // Étape 1 : Créer un DTO simulé
        UserDto dto = new UserDto();
        dto.setId(1);
        System.out.println("ÉTAPE 1 : DTO utilisateur simulé avec ID = " + dto.getId());

        // Étape 2 : Simuler la récupération de l'utilisateur
        when(userService.getUserById(1)).thenReturn(dto);
        System.out.println("ÉTAPE 2 : Simulation du service userService.getUserById(1)");

        // Étape 3 : Appeler la méthode du contrôleur
        ResponseEntity<UserDto> response = controller.getUser(1);
        System.out.println("ÉTAPE 3 : Méthode controller.getUser(1) appelée.");

        // Étape 4 : Vérifier le code de réponse et l'ID retourné
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getId());
        System.out.println("RÉSULTAT : testGetUser_found passé avec succès. ID retourné = " + response.getBody().getId() + "\n");
    }

    @Test
    void testGetUser_notFound() {
        System.out.println("TEST : testGetUser_notFound démarré.");

        // Étape 1 : Simuler un utilisateur inexistant
        when(userService.getUserById(999)).thenReturn(null);
        System.out.println("ÉTAPE 1 : Simulation - aucun utilisateur trouvé pour l'ID 999.");

        // Étape 2 : Appeler la méthode du contrôleur
        ResponseEntity<UserDto> response = controller.getUser(999);
        System.out.println("ÉTAPE 2 : Appel de controller.getUser(999).");

        // Étape 3 : Vérification du statut HTTP 404
        assertEquals(404, response.getStatusCodeValue());
        System.out.println("RÉSULTAT : " +
                "testGetUser_notFound passé avec succès. Statut HTTP = " +
                response.getStatusCodeValue() + "\n"
        );
    }

    @Test
    void testGetUserByEmail() {
        System.out.println("TEST : testGetUserByEmail démarré.");

        // Étape 1 : Préparer un utilisateur avec un email
        User user = new User();
        user.setEmail("abc@xyz.com");
        System.out.println("ÉTAPE 1 : Utilisateur simulé avec email = " + user.getEmail());

        // Étape 2 : Simuler la recherche par email
        when(userService.getUserByEmail("abc@xyz.com")).thenReturn(user);
        System.out.println("ÉTAPE 2 : Simulation du service userService.getUserByEmail(\"abc@xyz.com\")");

        // Étape 3 : Appeler la méthode du contrôleur
        User result = controller.getUserByEmail("abc@xyz.com");
        System.out.println("ÉTAPE 3 : Appel de controller.getUserByEmail(\"abc@xyz.com\")");

        // Étape 4 : Vérification de l'email retourné
        assertEquals("abc@xyz.com", result.getEmail());
        System.out.println("RÉSULTAT : " +
                "testGetUserByEmail passé avec succès. Email retourné = " +
                result.getEmail() + "\n"
        );
    }

    @Test
    void testGetUsersByNameOrFirstName() {
        System.out.println("TEST : testGetUsersByNameOrFirstName démarré.");

        // Étape 1 : Préparer une liste d'utilisateurs simulée
        List<User> list = List.of(new User(), new User());
        System.out.println("ÉTAPE 1 : Liste d'utilisateurs simulée avec " + list.size() + " utilisateurs.");

        // Étape 2 : Simuler la recherche par nom/prénom
        when(userService.getUsersByNameOrFirstNameContaining("a")).thenReturn(list);
        System.out.println("ÉTAPE 2 : Simulation du service userService.getUsersByNameOrFirstNameContaining(\"a\")");

        // Étape 3 : Appeler la méthode du contrôleur
        ResponseEntity<List<User>> response = controller.getUsersByNameOrFirstName("a");
        System.out.println("ÉTAPE 3 : Appel de controller.getUsersByNameOrFirstName(\"a\")");

        // Étape 4 : Vérifier le nombre d'utilisateurs retournés
        assertEquals(2, response.getBody().size());
        System.out.println("RÉSULTAT : " +
                "testGetUsersByNameOrFirstName passé avec succès. Nombre d'utilisateurs retournés : " +
                response.getBody().size() + "\n"
        );
    }

    @Test
    void testUpdateUser() {
        System.out.println("TEST : testUpdateUser démarré.");

        // Étape 1 : Créer un utilisateur fictif mis à jour
        User user = new User();
        user.setId(1);
        System.out.println("ÉTAPE 1 : Utilisateur fictif préparé avec ID = " + user.getId());

        // Étape 2 : Simuler la mise à jour
        when(userService.updateUser(eq(1), any())).thenReturn(user);
        System.out.println("ÉTAPE 2 : Simulation du service userService.updateUser(1, ...)");

        // Étape 3 : Appeler la méthode du contrôleur
        ResponseEntity<User> response = controller.updateUser(1, new UserDto());
        System.out.println("ÉTAPE 3 : Appel de controller.updateUser(1, userDto)");

        // Étape 4 : Vérifier l'ID de l'utilisateur mis à jour
        assertEquals(1, response.getBody().getId());
        System.out.println("RÉSULTAT : " +
                "testUpdateUser passé avec succès. ID retourné = " +
                response.getBody().getId() + "\n"
        );
    }

    @Test
    void testDeleteUser() {
        System.out.println("TEST : testDeleteUser démarré.");

        // Étape 1 : Simuler la suppression de l'utilisateur
        doNothing().when(userService).deleteUser(1);
        System.out.println("ÉTAPE 1 : Simulation de la suppression de l'utilisateur avec ID = 1");

        // Étape 2 : Appeler la méthode du contrôleur
        ResponseEntity<Void> response = controller.deleteUser(1);
        System.out.println("ÉTAPE 2 : Appel de controller.deleteUser(1)");

        // Étape 3 : Vérifier le statut de réponse HTTP 204 (No Content)
        assertEquals(204, response.getStatusCodeValue());
        verify(userService).deleteUser(1);
        System.out.println("RÉSULTAT : " +
                "testDeleteUser passé avec succès. Statut HTTP = " +
                response.getStatusCodeValue() + "\n"
        );
    }

    @Test
    void testDeleteUserByEmail() {
        System.out.println("TEST : testDeleteUserByEmail démarré.");

        // Étape 1 : Simuler la suppression via email
        doNothing().when(userService).deleteUserByEmail("x@y.com");
        System.out.println("ÉTAPE 1 : Simulation de la suppression de l'utilisateur avec l'email 'x@y.com'");

        // Étape 2 : Appeler la méthode du contrôleur
        ResponseEntity<String> response = controller.deleteUserByEmail("x@y.com");
        System.out.println("ÉTAPE 2 : Appel de controller.deleteUserByEmail(\"x@y.com\")");

        // Étape 3 : Vérifier le message de confirmation et le code de retour
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("x@y.com"));
        System.out.println("RÉSULTAT : testDeleteUserByEmail passé avec succès. Message = " + response.getBody() + "\n");
    }

    @Test
    void testConvertToDto() {
        System.out.println("TEST : testConvertToDto démarré.");

        // Étape 1 : Créer un utilisateur complet
        User user = new User();
        user.setId(1);
        user.setEmail("test@x.com");
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        System.out.println("ÉTAPE 1 : Utilisateur simulé -> ID: " + user.getId() + ", Prénom: " + user.getFirstName() +
                ", Nom: " + user.getLastName() + ", Email: " + user.getEmail());

        // Étape 2 : Appeler la méthode de conversion
        UserDto dto = controller.convertToDto(user);
        System.out.println("ÉTAPE 2 : Méthode convertToDto() appelée.");

        // Étape 3 : Vérifier que tous les champs sont correctement copiés
        assertEquals("Jean", dto.getFirstName());
        assertEquals("Dupont", dto.getLastName());
        assertEquals("test@x.com", dto.getEmail());
        System.out.println("RÉSULTAT : testConvertToDto passé avec succès. Données DTO : " +
                dto.getFirstName() + " " + dto.getLastName() + ", " + dto.getEmail() + "\n");
    }

}
