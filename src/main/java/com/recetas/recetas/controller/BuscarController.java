package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para búsqueda de recetas
 * Implementa protecciones OWASP Top 10:
 * - A03:2021 Injection (validación y sanitización de entrada)
 */
@Controller
public class BuscarController {

    @Autowired
    private RecetaService recetaService;

    /**
     * Endpoint de búsqueda con validación de entrada
     * OWASP A03:2021 - Injection Prevention
     */
    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingrediente,
            @RequestParam(required = false) String paisOrigen,
            @RequestParam(required = false) String dificultad,
            Model model) {

        List<Receta> resultados = new ArrayList<>();

        // OWASP A03: Validación y sanitización de entrada
        if (nombre != null && !nombre.isEmpty()) {
            nombre = sanitizeInput(nombre);
            if (isValidInput(nombre)) {
                resultados = recetaService.buscarPorNombre(nombre);
            }
        } else if (tipoCocina != null && !tipoCocina.isEmpty()) {
            tipoCocina = sanitizeInput(tipoCocina);
            if (isValidInput(tipoCocina)) {
                resultados = recetaService.buscarPorTipoCocina(tipoCocina);
            }
        } else if (ingrediente != null && !ingrediente.isEmpty()) {
            ingrediente = sanitizeInput(ingrediente);
            if (isValidInput(ingrediente)) {
                resultados = recetaService.buscarPorIngrediente(ingrediente);
            }
        } else if (paisOrigen != null && !paisOrigen.isEmpty()) {
            paisOrigen = sanitizeInput(paisOrigen);
            if (isValidInput(paisOrigen)) {
                resultados = recetaService.buscarPorPaisOrigen(paisOrigen);
            }
        } else if (dificultad != null && !dificultad.isEmpty()) {
            dificultad = sanitizeInput(dificultad);
            if (isValidInput(dificultad)) {
                resultados = recetaService.buscarPorDificultad(dificultad);
            }
        } else {
            resultados = recetaService.obtenerTodasLasRecetas();
        }

        // Sanitizar datos antes de agregar al modelo (protección XSS adicional)
        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre != null ? HtmlUtils.htmlEscape(nombre) : "");
        model.addAttribute("tipoCocina", tipoCocina != null ? HtmlUtils.htmlEscape(tipoCocina) : "");
        model.addAttribute("ingrediente", ingrediente != null ? HtmlUtils.htmlEscape(ingrediente) : "");
        model.addAttribute("paisOrigen", paisOrigen != null ? HtmlUtils.htmlEscape(paisOrigen) : "");
        model.addAttribute("dificultad", dificultad != null ? HtmlUtils.htmlEscape(dificultad) : "");

        return "buscar";
    }
    
    /**
     * Sanitiza la entrada para prevenir XSS e Injection
     * OWASP A03:2021 - Injection
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Eliminar caracteres peligrosos
        return input.trim()
                .replaceAll("[<>\"';\\\\]", "")
                .substring(0, Math.min(input.length(), 100)); // Limitar longitud
    }
    
    /**
     * Valida que la entrada no contenga patrones maliciosos
     * OWASP A03:2021 - Injection
     */
    private boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        // Rechazar entrada con patrones SQL o script maliciosos
        String lowerInput = input.toLowerCase();
        return !lowerInput.contains("script") &&
               !lowerInput.contains("select") &&
               !lowerInput.contains("drop") &&
               !lowerInput.contains("insert") &&
               !lowerInput.contains("update") &&
               !lowerInput.contains("delete") &&
               !lowerInput.contains("exec") &&
               !lowerInput.contains("union");
    }
}
