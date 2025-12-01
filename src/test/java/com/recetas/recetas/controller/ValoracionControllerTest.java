package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.ValoracionRequest;
import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ValoracionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValoracionController.class)
class ValoracionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ValoracionService valoracionService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ValoracionResponse valoracionResponse;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        
        valoracionResponse = new ValoracionResponse();
        valoracionResponse.setPromedio(4.5);
        valoracionResponse.setTotalValoraciones(10L);
        valoracionResponse.setMiValoracion(5);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCrearOActualizarValoracion() throws Exception {
        // Arrange
        ValoracionRequest request = new ValoracionRequest();
        request.setRecetaId(1L);
        request.setPuntuacion(5);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(valoracionService).crearOActualizarValoracion(anyLong(), anyLong(), anyInt());
        
        // Act & Assert
        mockMvc.perform(post("/api/valoraciones")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        verify(valoracionService, times(1)).crearOActualizarValoracion(eq(1L), eq(1L), eq(5));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testObtenerValoraciones() throws Exception {
        // Arrange
        when(valoracionService.obtenerValoracionesPorReceta(1L, null)).thenReturn(valoracionResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/valoraciones/receta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.totalValoraciones").value(10));
        
        verify(valoracionService, times(1)).obtenerValoracionesPorReceta(eq(1L), any());
    }
}

