package com.recetas.recetas.controller;

import com.recetas.recetas.dto.RecetaRequest;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de recetas
 * API privada: POST /api/recetas (requiere autenticación JWT)
 */
@RestController
@RequestMapping("/api/recetas")
public class RecetaApiController {
    
    @Autowired
    private RecetaService recetaService;
    
    /**
     * API privada para publicar una receta
     * POST /api/recetas
     * Requiere autenticación JWT
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearReceta(@Valid @RequestBody RecetaRequest recetaRequest) {
        try {
            // Crear nueva receta
            Receta receta = new Receta();
            receta.setNombre(recetaRequest.getNombre());
            receta.setIngredientes(recetaRequest.getIngredientes());
            receta.setInstrucciones(recetaRequest.getInstrucciones());
            receta.setTipoCocina(recetaRequest.getTipoCocina());
            receta.setPaisOrigen(recetaRequest.getPaisOrigen());
            receta.setTiempoCoccion(recetaRequest.getTiempoCoccion());
            receta.setDificultad(recetaRequest.getDificultad());
            
            // Guardar receta
            Receta recetaGuardada = recetaService.guardarReceta(receta);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(recetaGuardada);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear receta: " + e.getMessage());
        }
    }
}

