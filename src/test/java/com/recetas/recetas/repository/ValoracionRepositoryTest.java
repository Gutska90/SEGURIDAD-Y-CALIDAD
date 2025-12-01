package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.model.Valoracion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ValoracionRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private ValoracionRepository valoracionRepository;
    
    
    private Receta receta;
    private Usuario usuario;
    private Valoracion valoracion;
    
    @BeforeEach
    void setUp() {
        // Crear rol
        Role role = new Role();
        role.setNombre("ROLE_USER");
        entityManager.persist(role);
        
        // Crear usuario
        usuario = new Usuario();
        usuario.setNombreCompleto("Test User");
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password");
        usuario.setEnabledBoolean(true);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        usuario.setRoles(roles);
        entityManager.persist(usuario);
        
        // Crear receta
        receta = new Receta();
        receta.setNombre("Paella");
        receta.setTipoCocina("Mediterránea");
        receta.setPaisOrigen("España");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(60);
        receta.setIngredientes("Arroz");
        receta.setInstrucciones("Cocinar");
        receta.setFechaCreacion(LocalDateTime.now());
        entityManager.persist(receta);
        
        entityManager.flush();
        
        // Crear valoración
        valoracion = new Valoracion();
        valoracion.setReceta(receta);
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(5);
        valoracion.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    void testSave() {
        // Act
        Valoracion guardada = valoracionRepository.save(valoracion);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardada.getId());
        assertEquals(5, guardada.getPuntuacion());
    }
    
    @Test
    void testFindByRecetaIdAndUsuarioId() {
        // Arrange
        entityManager.persist(valoracion);
        entityManager.flush();
        
        // Act
        Optional<Valoracion> resultado = valoracionRepository.findByRecetaIdAndUsuarioId(receta.getId(), usuario.getId());
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(5, resultado.get().getPuntuacion());
    }
    
    @Test
    void testContarPorRecetaId() {
        // Arrange
        entityManager.persist(valoracion);
        entityManager.flush();
        
        // Act
        long count = valoracionRepository.contarPorRecetaId(receta.getId());
        
        // Assert
        assertEquals(1L, count);
    }
}