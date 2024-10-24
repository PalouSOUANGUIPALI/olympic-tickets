package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.config.JwtService;
import com.infoevent.olympictickets.dto.AuthentificationDTO;
import com.infoevent.olympictickets.dto.UserDto;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.enums.RoleType;
import com.infoevent.olympictickets.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "managment/users")
public class UserManagementController {

    @Value("${olympic.tickets.base.url}")
    private String olympicTicketsBaseUrl;


    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public UserManagementController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Création de compte
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> inscription(@RequestBody UserDto userDto) {
        UserDto createdUser = this.userService.inscription(userDto); // Appel à la méthode de service qui retourne le nouvel utilisateur

        Map<String, Object> response = new HashMap<>();
        response.put("id", createdUser.getId()); // Récupère l'ID du nouvel utilisateur
        response.put("securityKey", createdUser.getSecurityKey()); // Récupère le securityKey du nouvel utilisateur
        return ResponseEntity.ok(response);
    }


    // Activation ou validation de compte avec un code envoyé sur la boîte mail de l'utilisateur
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "activation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activation(@RequestBody Map<String, String> activation) {
        this.userService.activation(activation);
    }



    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "connexion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );

        if (authenticate.isAuthenticated()) {
            User user = userService.getUserByEmail(authentificationDTO.username());

            // Vérifiez le rôle de l'utilisateur et préparez la redirection
            String redirectUrl;
            if (user.getRole().getLabel() == RoleType.ADMINISTRATEUR) {
                // Utilisez la base URL pour les administrateurs
                redirectUrl = olympicTicketsBaseUrl + "/offers/management";
            } else {
                // Utilisez la base URL pour les autres utilisateurs
                redirectUrl = olympicTicketsBaseUrl + "/users/offres";
            }

            // Générer le JWT
            Map<String, String> jwtResponse = this.jwtService.generate(authentificationDTO.username());

            // Inclure l'URL de redirection et l'ID utilisateur ou administrateur dans la réponse
            jwtResponse.put("redirectUrl", redirectUrl);
            jwtResponse.put("userId", String.valueOf(user.getId())); // Ajoutez l'ID de l'utilisateur

            return ResponseEntity.ok(jwtResponse); // Renvoie le token et l'URL de redirection
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "L'authentification a échoué")); // Renvoie d'erreur
    }









    /*@ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "connexion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );

        if (authenticate.isAuthenticated()) {
            User user = userService.getUserByEmail(authentificationDTO.username());

            // Vérifiez le rôle de l'utilisateur et préparez la redirection
            String redirectUrl;
            if (user.getRole().getLabel() == RoleType.ADMINISTRATEUR) {
                redirectUrl = "http://localhost:1991/api/offers/management"; // URL pour les administrateurs
            } else {
                redirectUrl = "http://localhost:1991/api/users/offres"; // URL pour les autres utilisateurs
            }

            // Générer le JWT
            Map<String, String> jwtResponse = this.jwtService.generate(authentificationDTO.username());

            // Inclure l'URL de redirection et l'ID utilisateur ou administrateur dans la réponse
            jwtResponse.put("redirectUrl", redirectUrl);
            jwtResponse.put("userId", String.valueOf(user.getId())); // Ajoutez l'ID de l'utilisateur

            return ResponseEntity.ok(jwtResponse); // Renvoie le token et l'URL de redirection
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "L'authentification a échoué")); // Renvoie d'erreur
    }

     */


    // Deconnexion du user
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "deconnexion")
    public void deconnexion() {
        this.jwtService.deconnexion();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(@RequestBody Map<String, String> changePassword) {
        this.userService.passwordChanging(changePassword);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "new-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setNewPassword(@RequestBody Map<String, String> newPassword) {
        this.userService.makeNewPassword(newPassword);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "refresh-token")
    public @ResponseBody Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return this.jwtService.refreshToken(refreshTokenRequest);
    }


    // Récupération de tous les utilisateurs de la base de données
    @GetMapping(path = "get-all-users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/get-user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        UserDto userDto = userService.getUserById(id);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Rechercher un user par son email
    @GetMapping(path = "get-user-by-email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);

    }

    // Méthode pour rechercher les utilisateurs par lettres dans leur nom ou prénom
    @GetMapping(path = "get-users-by-name-or-firstname/{searchTerm}")
    public ResponseEntity<List<User>> getUsersByNameOrFirstName(@PathVariable String searchTerm) {
        List<User> users = userService.getUsersByNameOrFirstNameContaining(searchTerm);
        //log.info("getUsersByNameOrFirstName: " + users);
        return ResponseEntity.ok(users);
    }



    //Mettre à jour utilisateur
    @PutMapping("update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // Supprimer un user à travers son id
    //@ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("delete-user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete-user-by-email/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        this.userService.deleteUserByEmail(email);
        return ResponseEntity.ok("L'utilisateur avec l'email " + email + " a été supprimé avec succès.");
    }

    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        return userDto;
    }
}
