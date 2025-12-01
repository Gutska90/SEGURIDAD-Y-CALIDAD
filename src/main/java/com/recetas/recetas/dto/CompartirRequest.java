package com.recetas.recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para compartir una receta
 */
public class CompartirRequest {
    
    @NotNull(message = "El ID de la receta es obligatorio")
    private Long recetaId;
    
    @NotBlank(message = "La plataforma es obligatoria")
    private String plataforma; // facebook, twitter, whatsapp, web, etc.
    
    public CompartirRequest(Long recetaId, String plataforma) {
        this.recetaId = recetaId;
        this.plataforma = plataforma;
    }
    
    public Long getRecetaId() {
        return recetaId;
    }
    
    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }
    
    public String getPlataforma() {
        return plataforma;
    }
    
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
}

