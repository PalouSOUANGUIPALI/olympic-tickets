package com.infoevent.olympictickets;

import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.entity.UserRole;
import com.infoevent.olympictickets.enums.RoleType;
import com.infoevent.olympictickets.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDateTime;
import java.util.Optional;

@EnableScheduling
@SpringBootApplication
public class OlympicTicketsApplication implements CommandLineRunner {


	UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public OlympicTicketsApplication(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
    }
	public static void main(String[] args) {
		SpringApplication.run(OlympicTicketsApplication.class, args);
	}

	// Initialisation d'un administrateur à chaque démarrage de l'application
	// Je l'ai implémenté comme ça juste pour le besoin de l'examen :
	// sinon en production d'un site ce n'est pas la meilleure pratique.
	// Je pourrais précharger les role dans la base et mettre aussi en place une interface des gestion CRUD pour cela
	@Override
	public void run(String... args) throws Exception {
		// Vérifiez si l'administrateur existe déjà
		Optional<User> existingAdmin = this.userRepository.findByEmail("noreply.olympic.tickets@gmail.com");

		// Si l'administrateur n'existe pas, créez-le
		if (existingAdmin.isEmpty()) {
			User admin = User.builder()
					.active(true)
					.email("noreply.olympic.tickets@gmail.com")
					.password(bCryptPasswordEncoder.encode("admin")) // Assurez-vous que le mot de passe est sécurisé
					.firstName("admin_name")
					.lastName("admin_last_name")
					.createdAt(LocalDateTime.now())
					.role(UserRole.builder()
							.label(RoleType.ADMINISTRATEUR)
							.build())
					.build();

			// Sauvegardez le nouvel administrateur
			this.userRepository.save(admin);
			System.out.println("Administrateur créé : " + admin.getEmail());
		} else {
			System.out.println("L'administrateur existe déjà : " + existingAdmin.get().getEmail());
		}
	}

}
