package com.recetas.recetas.repository;

import com.recetas.recetas.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RoleRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Test
    void testFindByNombre() {
        // Arrange
        Role role = new Role();
        role.setNombre("ROLE_USER");
        entityManager.persist(role);
        entityManager.flush(); 
        
        // Act
        Optional<Role> resultado = roleRepository.findByNombre("ROLE_USER");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("ROLE_USER", resultado.get().getNombre());
    }
    
    @Test
    void testFindByNombre_NoEncontrado() {
        // Act
        Optional<Role> resultado = roleRepository.findByNombre("ROLE_NONEXISTENT");
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void testSave() {
        // Arrange
        Role role = new Role();
        role.setNombre("ROLE_TEST");
        
        // Act
        Role guardado = roleRepository.save(role);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardado.getId());
        assertEquals("ROLE_TEST", guardado.getNombre());
    }
}