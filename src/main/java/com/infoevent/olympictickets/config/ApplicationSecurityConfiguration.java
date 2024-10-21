package com.infoevent.olympictickets.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@EnableTransactionManagement
@EnableWebSecurity
public class ApplicationSecurityConfiguration implements WebMvcConfigurer {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public ApplicationSecurityConfiguration(BCryptPasswordEncoder bCryptPasswordEncoder, JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Autoriser toutes les requêtes
                .allowedOrigins("http://localhost:1992") //  l'URL front-end backOffice
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return
                httpSecurity
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(
                                authorize ->
                                        authorize
                                                .requestMatchers(POST,"managment/users/register").permitAll() // Route API appelé par le template inscription.html
                                                .requestMatchers(POST,"managment/users/activation").permitAll() // Route API appelé par le template validation.html
                                                .requestMatchers(POST,"managment/users/connexion").permitAll() // Route API appelé par le template login.html
                                                .requestMatchers(POST,"managment/users/change-password").permitAll() // Route API -------   à faire si j'ai le temps car pas demandé dans le devoir  -----
                                                .requestMatchers(POST,"managment/users/new-password").permitAll() // Route API -------   à faire si j'ai le temps car pas demandé dans le devoir  -----
                                                .requestMatchers(POST,"managment/users/refresh-token").permitAll() // Route API -------   à faire si j'ai le temps car pas demandé dans le devoir  -----

                                                // Managment Users ------- A faire si j'ai le temps car pas demandé dans le devoir   ------------
                                                .requestMatchers(GET,"managment/users/get-all-users").permitAll() // Route API
                                                .requestMatchers(GET,"managment/users/get-user/{id}").permitAll() // Route API

                                                // Frontend Routes
                                                .requestMatchers(GET,"users/inscription").permitAll() // Route Front-end page html
                                                .requestMatchers(GET,"users/validation").permitAll() // Route Front-end page html
                                                .requestMatchers(GET,"/users/login").permitAll() // Route Front-end page html
                                                .requestMatchers(GET,"/users/home").permitAll() // Route Front-end page html
                                                .requestMatchers(GET,"/users/offres").permitAll() // Route Front-end page html
                                                .requestMatchers(GET,"/offers/billets").permitAll() // Route Front-end page html
                                                //.requestMatchers(GET,"/offers/duo").permitAll() // Route Front-end
                                                .requestMatchers(GET, "/offers/allOffers").permitAll() // Route Front-end page html
                                                .requestMatchers(POST, "/tickets/reserve").authenticated()
                                                .requestMatchers(GET, "/payment/ticket-payment").permitAll()
                                                .requestMatchers(POST, "/payment/process").permitAll() // Route Front-end page html

                                                // Backend Routes pour l'administrateur
                                                .requestMatchers(GET, "/offers/management").permitAll() // Route Front-end page html
                                                //.requestMatchers(GET, "/offers/sold-by-type").permitAll() // Route Pour retourner toutes les offres vendues par type
                                                //.requestMatchers(GET, "/sold-by-type").hasAnyAuthority("ROLE_ADMINISTRATEUR") // Autoriser uniquement les administrateurs
                                                .requestMatchers(GET, "/offers/allOffers-manage").hasAnyAuthority("ROLE_ADMINISTRATEUR") // Route Front-end page html
                                                .requestMatchers(PUT, "/offers/update/{id}").hasAnyAuthority("ROLE_ADMINISTRATEUR")
                                                .requestMatchers(GET, "/offers/get-offer/{id}").hasAnyAuthority("ROLE_ADMINISTRATEUR")
                                                .requestMatchers(POST, "/offers/create").hasAnyAuthority("ROLE_ADMINISTRATEUR")
                                                .requestMatchers(POST, "/offers/delete/{id}").hasAnyAuthority("ROLE_ADMINISTRATEUR")
                                                .requestMatchers(POST, "/offers/type/{offerType}").hasAnyAuthority("ROLE_ADMINISTRATEUR")

                                                .anyRequest().authenticated()
                        )
                        .sessionManagement(httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                        )
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return daoAuthenticationProvider;
    }

}
