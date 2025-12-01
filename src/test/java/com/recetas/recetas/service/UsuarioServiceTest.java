package com.recetas.recetas.service;

import com.recetas.recetas.dto.UsuarioRequest;
import com.recetas.recetas.dto.UsuarioResponse;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    private Role roleUser;
    private Role roleAdmin;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setNombre("ROLE_USER");
        
        roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setNombre("ROLE_ADMIN");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Test User");
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("encodedPassword");
        usuario.setEnabledBoolean(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        usuario.setRoles(roles);
    }
    
    @Test
    void testCrearUsuario() {
        // Arrange
        UsuarioRequest request = new UsuarioRequest();
        request.setNombreCompleto("Nuevo Usuario");
        request.setUsername("nuevousuario");
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");
        request.setRole("ROLE_USER");
        request.setEnabled(true);
        
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        // Act
        UsuarioResponse response = usuarioService.crearUsuario(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("password123");
    }
    
    @Test
    void testCrearUsuario_UsernameDuplicado() {
        // Arrange
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("testuser");
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.crearUsuario(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void testObtenerUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        // Act
        UsuarioResponse response = usuarioService.obtenerUsuarioPorId(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        verify(usuarioRepository).findById(1L);
    }
    
    @Test
    void testObtenerUsuarioPorId_NoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerUsuarioPorId(999L));
    }
    
    @Test
    void testActualizarUsuario() {
        // Arrange
        UsuarioRequest request = new UsuarioRequest();
        request.setNombreCompleto("Usuario Actualizado");
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("newPassword");
        request.setRole("ROLE_ADMIN");
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(roleRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        // Act
        UsuarioResponse response = usuarioService.actualizarUsuario(1L, request);
        
        // Assert
        assertNotNull(response);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("newPassword");
    }
    
    @Test
    void testEliminarUsuario() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);
        
        // Act
        usuarioService.eliminarUsuario(1L);
        
        // Assert
        verify(usuarioRepository).deleteById(1L);
    }
    
    @Test
    void testEliminarUsuario_NoEncontrado() {
        // Arrange
        when(usuarioRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.eliminarUsuario(999L));
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}

