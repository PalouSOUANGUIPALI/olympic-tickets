package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.dto.UserDto;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.UserRole;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.enums.RoleType;
import com.infoevent.olympictickets.exception.UserNotFoundException;
import com.infoevent.olympictickets.repository.JwtRepository;
import com.infoevent.olympictickets.repository.UserRepository;
import com.infoevent.olympictickets.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;



@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository; ///
    private final EmailService emailService; ////
    private final JwtRepository jwtRepository;


    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       ValidationService validationService,
                       ValidationRepository validationRepository,
                       EmailService emailService,
                       JwtRepository jwtRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.validationService = validationService;
        this.validationRepository = validationRepository; ////
        this.emailService = emailService; ////
        this.jwtRepository = jwtRepository;
    }

    // inscription
    public UserDto inscription(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        // Validation de l'email
        if (!userDto.getEmail().contains("@") || !userDto.getEmail().contains(".")) {
            throw new RuntimeException("Votre mail est invalide");
        }

        user.setEmail(userDto.getEmail());

        // Vérifier si l'utilisateur existe dans la base données
        Optional<User> userOptional = this.userRepository.findByEmail(userDto.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("Votre mail est déjà utilisé par un compte");
        }

        UserRole userRole = new  UserRole();
        userRole.setLabel(RoleType.UTILISATEUR);
        user.setRole(userRole);

        // Génération de la clé de sécurité
        String securityKey = generateSecurityKey();
        user.setSecurityKey(securityKey);

        // Sauvegarde de l'utilisateur
        user = this.userRepository.save(user);

        // Envoi de du code de validation dans ValidationService
        this.validationService.saveUserInValidationService(user);

        // Retourner l'utilisateur sous forme de UserDto,
        // afin de pouvoir enregistrer son securityKey (première clé d'encode du billet)
        UserDto newUserDto = new UserDto();
        newUserDto.setId(user.getId());
        newUserDto.setSecurityKey(user.getSecurityKey());
        newUserDto.setFirstName(user.getFirstName());
        newUserDto.setLastName(user.getLastName());
        newUserDto.setEmail(user.getEmail());

        return newUserDto;


    }

    // La première clé d'encodage du qr code : clé générée lors de l'inscription
    public String generateSecurityKey() {

        // Utilisation SecureRandom pour générer une clé sécurisée
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24]; // Taille de la clé en octets
        secureRandom.nextBytes(randomBytes);

        // Conversion en chaîne de caractères
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    // Validation ou activation de compte
    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.userActivationCodeVerification(activation.get("code"));
        if(Instant.now().isAfter(validation.getExpirationTime())){
            throw  new RuntimeException("Votre code a expiré");

        }
        User userToActivate = this.userRepository.findById(validation.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
        userToActivate.setActive(true);
        this.userRepository.save(userToActivate);
        this.validationService.deleteValidation(validation.getId());
    }


    // Changement de mot de passe utilisateur
   public void passwordChanging(Map<String, String> params) {
        User userChangingPassword = loadUserByUsername(params.get("email"));
        this.validationService.saveUserInValidationService(userChangingPassword);
    }

    // Nouveau mot de passe
    public void makeNewPassword(Map<String, String> paramters) {
        User userNewPassword = loadUserByUsername(paramters.get("email"));
        final Validation validation = this.validationService.userActivationCodeVerification(paramters.get("code"));
        if(validation.getUser().getEmail().equals(userNewPassword.getEmail())){
            String newPassword = this.bCryptPasswordEncoder.encode(paramters.get("password"));
            userNewPassword.setPassword(newPassword);
            this.userRepository.save(userNewPassword);
        }

    }


    // Obtenir tous les users
    public List<User> getAllUsers() {
        List<User> users = (List<User>) userRepository.findAll(); // Récupérer tous les utilisateurs
        users.forEach(System.out::println); // Afficher chaque utilisateur dans la console
        return users; // Retourner la liste des utilisateurs
    }

    // Obtenir un user par son id
    public UserDto getUserById(Integer id) {
        UserDto userDto = new UserDto();
        User user = this.userRepository.findById(id).orElse(null);
        assert user != null;

        userDto.setId(user.getId());
                userDto.setFirstName(user.getFirstName());
                userDto.setLastName(user.getLastName());
                userDto.setEmail(user.getEmail());
                userDto.setFirstName(user.getFirstName());
        return userDto;
    }

    // Obtenir un user par son email
    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }


    // Méthode pour rechercher les utilisateurs par lettres dans leur nom ou prénom
    @Transactional
    public List<User> getUsersByNameOrFirstNameContaining(String searchTerm) {
        return userRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(searchTerm, searchTerm);

    }


    // Mettre à jour un utilisateur
    public User updateUser (Integer id, UserDto userDto) throws UsernameNotFoundException{
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new  UsernameNotFoundException("Aucun utilisateur trouvé"));;
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword())); // Hash password
        return userRepository.save(user);
    }

    // Supprimer un user par son id
    @Transactional
    public void deleteUser (Integer id){
        this.validationService.updateUserIdToNull(id);

        // Met à jour les jwt pour supprimer la référence à cet utilisateur
        jwtRepository.updateUserIdToNull(id);

        userRepository.deleteById(id);
    }

    // Supprimer un user par son mail
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email : " + email));
        // Met à jour les jwt pour supprimer la référence à cet utilisateur
        jwtRepository.updateUserIdToNull(user.getId());
        userRepository.delete(user);
    }


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository
                .findByEmail(username)
                .orElseThrow(() -> new  UsernameNotFoundException("Aucun utilisateur ne corespond à cet identifiant"));
    }

}
