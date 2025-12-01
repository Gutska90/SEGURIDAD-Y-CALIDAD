package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.LoginRequest;
import com.recetas.recetas.dto.RegistroRequest;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @MockBean
    private RoleRepository roleRepository;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private DetalleUserService userDetailsService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Usuario usuario;
    private Role roleUser;
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setNombre("ROLE_USER");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("encodedPassword");
        usuario.setEnabledBoolean(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        usuario.setRoles(roles);
        
        userDetails = User.withUsername("testuser")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();
    }
    
    @Test
    void testLogin() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    void testRegistro() throws Exception {
        // Arrange
        RegistroRequest request = new RegistroRequest();
        request.setNombreCompleto("Nuevo Usuario");
        request.setUsername("nuevousuario");
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");
        
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(userDetailsService.loadUserByUsername("nuevousuario")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }
}

