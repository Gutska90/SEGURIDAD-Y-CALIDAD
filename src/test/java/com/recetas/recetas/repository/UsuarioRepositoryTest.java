package com.recetas.recetas.repository;

import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    private Role roleUser;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        // Crear rol
        roleUser = new Role();
        roleUser.setNombre("ROLE_USER");
        entityManager.persist(roleUser);
        entityManager.flush(); 
        
        // Crear usuario de prueba
        usuario = new Usuario();
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
    void testFindByUsername() {
        // Arrange
        entityManager.persist(usuario);
        entityManager.flush();
        
        // Act
        Optional<Usuario> resultado = usuarioRepository.findByUsername("testuser");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getUsername());
    }
    
    @Test
    void testFindByUsername_NoEncontrado() {
        // Act
        Optional<Usuario> resultado = usuarioRepository.findByUsername("nonexistent");
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void testFindByEmail() {
        // Arrange
        entityManager.persist(usuario);
        entityManager.flush();
        
        // Act
        Optional<Usuario> resultado = usuarioRepository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("test@example.com", resultado.get().getEmail());
    }
    
    @Test
    void testSave() {
        // Act
        Usuario guardado = usuarioRepository.save(usuario);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardado.getId());
        assertEquals("testuser", guardado.getUsername());
    }
}