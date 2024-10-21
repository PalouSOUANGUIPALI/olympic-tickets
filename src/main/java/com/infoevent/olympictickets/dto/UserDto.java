package com.infoevent.olympictickets.dto;

import com.infoevent.olympictickets.entity.User;
import jakarta.persistence.Column;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {

    private int id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    /////

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;

    // Clé de sécurisation du billet
    private String securityKey;

    public UserDto(int id, String firstName, String lastName, String email) {
    }



    // Getters et Setters generated with lombok


    // Méthode de service pour convertir User en UserDto
    public static UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }
}
