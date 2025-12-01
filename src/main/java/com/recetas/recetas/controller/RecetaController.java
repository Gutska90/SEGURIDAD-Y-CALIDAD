package com.recetas.recetas.controller;


import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para visualización de recetas
 * Implementa protecciones OWASP Top 10:
 * - A01:2021 Broken Access Control (protegido por Spring Security)
 * - A03:2021 Injection (validación de entrada)
 * - A09:2021 Security Logging and Monitoring
 */
@Controller
@RequestMapping("/recetas")
public class RecetaController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecetaController.class);
    
    @Autowired
    private RecetaService recetaService;

    /**
     * Ver detalle de una receta (requiere autenticación)
     * OWASP A01:2021 - Broken Access Control
     * OWASP A03:2021 - Injection (validación de ID)
     * OWASP A09:2021 - Security Logging
     */
    @GetMapping("/{id}")
    public String verDetalleReceta(@PathVariable Long id, Model model) {
        // OWASP A03: Validación de entrada
        if (id == null || id <= 0) {
            logger.warn("Intento de acceso con ID inválido: {}", id);
            return "redirect:/buscar?error=id_invalido";
        }
        
        try {
            Receta receta = recetaService.obtenerRecetaPorId(id)
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
            
            model.addAttribute("receta", receta);
            
            // OWASP A09: Logging de acciones de usuario (sin datos sensibles)
            logger.info("Usuario accedió a receta ID: {}", id);
            
            return "detalle-receta";
        } catch (Exception e) {
            // OWASP A09: Logging de errores
            logger.error("Error al obtener receta ID {}: {}", id, e.getMessage());
            return "redirect:/buscar?error=receta_no_encontrada";
        }
    }
}
