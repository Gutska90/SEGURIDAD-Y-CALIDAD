package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.RecetaRequest;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaApiController.class)
class RecetaApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private RecetaService recetaService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Receta receta;
    private RecetaRequest recetaRequest;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella Valenciana");
        receta.setTipoCocina("Mediterránea");
        receta.setPaisOrigen("España");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(60);
        receta.setIngredientes("Arroz, pollo, mariscos");
        receta.setInstrucciones("1. Sofreír el pollo");
        
        recetaRequest = new RecetaRequest();
        recetaRequest.setNombre("Paella Valenciana");
        recetaRequest.setTipoCocina("Mediterránea");
        recetaRequest.setPaisOrigen("España");
        recetaRequest.setDificultad("Media");
        recetaRequest.setTiempoCoccion(60);
        recetaRequest.setIngredientes("Arroz, pollo, mariscos");
        recetaRequest.setInstrucciones("1. Sofreír el pollo");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testCrearReceta() throws Exception {
        // Arrange
        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(receta);
        
        // Act & Assert
        mockMvc.perform(post("/api/recetas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recetaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Paella Valenciana"));
        
        verify(recetaService, times(1)).guardarReceta(any(Receta.class));
    }
}

