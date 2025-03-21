package com.infoevent.olympictickets.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        // Étape 1 : Initialiser le contrôleur avant chaque test
        userController = new UserController();
        System.out.println("INITIALISATION : UserController initialisé avec succès.");
    }

    @AfterEach
    void tearDown() {
        // Étape finale après chaque test
        System.out.println("TEARDOWN : Test de la méthode terminé avec succès.\n");
    }

    @Test
    void testShowHomePage() {
        System.out.println("TEST : testShowHomePage démarré.");

        // Étape 2 : Appel de la méthode
        String viewName = userController.showHomePage();

        // Étape 3 : Vérification de la vue retournée
        assertEquals("index", viewName);

        System.out.println("INFO : Vue retournée = " + viewName);
        System.out.println("TEST : testShowHomePage terminé avec succès.");
    }

    @Test
    void testShowInscriptionPage() {
        System.out.println("TEST : testShowInscriptionPage démarré.");

        // Étape 2 : Appel de la méthode
        String viewName = userController.showInscriptionPage();

        // Étape 3 : Vérification de la vue
        assertEquals("inscription", viewName);

        System.out.println("INFO : Vue retournée = " + viewName);
        System.out.println("TEST : testShowInscriptionPage terminé.");
    }

    @Test
    void testShowValidationPage() {
        System.out.println("TEST : testShowValidationPage démarré.");

        // Étape 2 : Appel de la méthode
        String viewName = userController.showValidationPage();

        // Étape 3 : Assertion
        assertEquals("validation", viewName);

        System.out.println("INFO : Vue retournée = " + viewName);
        System.out.println("TEST : testShowValidationPage terminé avec succès.");
    }

    @Test
    void testShowLoginPage() {
        System.out.println("TEST : testShowLoginPage démarré.");

        // Étape 2 : Appel de la méthode
        String viewName = userController.showLoginPage();

        // Étape 3 : Assertion
        assertEquals("login", viewName);

        System.out.println("INFO : Vue retournée = " + viewName);
        System.out.println("TEST : testShowLoginPage terminé avec succès.");
    }

    @Test
    void testShowOffersPage() {
        System.out.println("TEST : testShowOffersPage démarré.");

        // Étape 2 : Appel de la méthode
        String viewName = userController.showOffersPage();

        // Étape 3 : Vérification
        assertEquals("offers", viewName);

        System.out.println("INFO : Vue retournée = " + viewName);
        System.out.println("TEST : testShowOffersPage terminé avec succès.");
    }
}
