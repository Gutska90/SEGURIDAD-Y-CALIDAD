package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.UsuarioRequest;
import com.recetas.recetas.dto.UsuarioResponse;
import com.recetas.recetas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UsuarioService usuarioService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private UsuarioResponse usuarioResponse;
    
    @BeforeEach
    void setUp() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setNombreCompleto("Test User");
        usuarioResponse.setUsername("testuser");
        usuarioResponse.setEmail("test@example.com");
        usuarioResponse.setEnabled(true);
        usuarioResponse.setRoles(roles);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerTodosLosUsuarios() throws Exception {
        // Arrange
        List<UsuarioResponse> usuarios = Arrays.asList(usuarioResponse);
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
        
        verify(usuarioService, times(1)).obtenerTodosLosUsuarios();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerUsuarioPorId() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(usuarioResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
        
        verify(usuarioService, times(1)).obtenerUsuarioPorId(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearUsuario() throws Exception {
        // Arrange
        UsuarioRequest request = new UsuarioRequest();
        request.setNombreCompleto("Nuevo Usuario");
        request.setUsername("nuevousuario");
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");
        request.setRole("ROLE_USER");
        
        when(usuarioService.crearUsuario(any(UsuarioRequest.class))).thenReturn(usuarioResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists());
        
        verify(usuarioService, times(1)).crearUsuario(any(UsuarioRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarUsuario() throws Exception {
        // Arrange
        UsuarioRequest request = new UsuarioRequest();
        request.setNombreCompleto("Usuario Actualizado");
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        
        when(usuarioService.actualizarUsuario(anyLong(), any(UsuarioRequest.class))).thenReturn(usuarioResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
        
        verify(usuarioService, times(1)).actualizarUsuario(eq(1L), any(UsuarioRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarUsuario() throws Exception {
        // Arrange
        doNothing().when(usuarioService).eliminarUsuario(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}

