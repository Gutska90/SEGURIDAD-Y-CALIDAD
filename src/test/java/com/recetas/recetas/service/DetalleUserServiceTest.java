package com.recetas.recetas.service;

import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalleUserServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private DetalleUserService detalleUserService;
    
    private Usuario usuario;
    private Role roleUser;
    
    @BeforeEach
    void setUp() {
        roleUser = new Role();
        roleUser.setNombre("ROLE_USER");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setEnabled(1);
        
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        usuario.setRoles(roles);
    }
    
    @Test
    void testLoadUserByUsername() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        
        // Act
        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");
        
        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
    
    @Test
    void testLoadUserByUsername_UsuarioNoEncontrado() {
        // Arrange
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            detalleUserService.loadUserByUsername("nonexistent"));
    }
}

