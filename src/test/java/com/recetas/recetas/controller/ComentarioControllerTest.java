package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.ComentarioRequest;
import com.recetas.recetas.dto.ComentarioResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComentarioController.class)
class ComentarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ComentarioService comentarioService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ComentarioResponse comentarioResponse;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setNombreCompleto("Test User");
        
        comentarioResponse = new ComentarioResponse();
        comentarioResponse.setId(1L);
        comentarioResponse.setRecetaId(1L);
        comentarioResponse.setUsuarioNombre("Test User");
        comentarioResponse.setComentario("Excelente receta!");
        comentarioResponse.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCrearComentario() throws Exception {
        // Arrange
        ComentarioRequest request = new ComentarioRequest();
        request.setRecetaId(1L);
        request.setComentario("Excelente receta!");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(comentarioService).crearComentario(anyLong(), anyLong(), anyString());
        
        // Act & Assert
        mockMvc.perform(post("/api/comentarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        
        verify(comentarioService, times(1)).crearComentario(eq(1L), eq(1L), anyString());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testObtenerComentarios() throws Exception {
        // Arrange
        when(comentarioService.obtenerComentariosPorReceta(1L))
                .thenReturn(Arrays.asList(comentarioResponse));
        
        // Act & Assert
        mockMvc.perform(get("/api/comentarios/receta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comentario").value("Excelente receta!"));
        
        verify(comentarioService, times(1)).obtenerComentariosPorReceta(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testEliminarComentario() throws Exception {
        // Arrange
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(comentarioService).eliminarComentario(1L, 1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/comentarios/1")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verify(comentarioService, times(1)).eliminarComentario(1L, 1L);
    }
}

