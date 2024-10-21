package com.infoevent.olympictickets.service;

import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.Validation;
import com.infoevent.olympictickets.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@AllArgsConstructor
@Service
public class ValidationService {

    private ValidationRepository validationRepository;


    private EmailService emailService;

    public void saveUserInValidationService(User user) {
        Validation validation = new Validation();

        validation.setUser(user);
        Instant creationTime = Instant.now();
        validation.setCreatedAt(creationTime);

        Instant expirationTime = creationTime.plus(5, MINUTES);
        validation.setExpirationTime(expirationTime);

        Instant activationTime = creationTime.plus(5, MINUTES);
        validation.setActivationTime(activationTime);

        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);
        validation.setCode(code);

        this.validationRepository.save(validation);
        emailService.sendValidationEmail(validation); // Envoyer l'e-mail
    }
    public Validation userActivationCodeVerification(String code) {

        return this.validationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }

    public void updateUserIdToNull(Integer id) {
        validationRepository.updateUserIdToNull(id);
    }



    @Scheduled(cron = "0 0 */10 * * *")
    public void removeUselessValidation() {
        this.validationRepository.deleteAll();
    }


    public void deleteValidation(int id) {
        this.validationRepository.deleteById(id);
    }
}
