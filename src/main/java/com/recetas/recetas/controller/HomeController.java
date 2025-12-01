package com.recetas.recetas.controller;

import com.recetas.recetas.service.AnuncioService;
import com.recetas.recetas.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private RecetaService recetaService;

    @Autowired
    private AnuncioService anuncioService;

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("recetasRecientes", recetaService.obtenerRecetasRecientes());
        model.addAttribute("recetasPopulares", recetaService.obtenerRecetasPopulares());
        model.addAttribute("anuncios", anuncioService.obtenerAnunciosActivos());
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
