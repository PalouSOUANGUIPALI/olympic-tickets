package com.infoevent.olympictickets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(value = "/users")
public class UserController {

    @GetMapping("/home")
    public String showHomePage() {
        return "index"; // Retourne le nom du template HTM (login.html)
    }

    @GetMapping("/inscription")
    public String showInscriptionPage() {
        return "inscription"; // Retourne le nom du template HTML (inscription.html)
    }

    @GetMapping("/validation")
    public String showValidationPage() {
        return "validation"; // Retourne le nom du template HTML (validation.html)
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Retourne le nom du template HTM (login.html)
    }

    @GetMapping("/offres")
    public String showOffersPage() {
        return "offers"; // Retourne le nom du template HTML (offers.html)
    }



}


