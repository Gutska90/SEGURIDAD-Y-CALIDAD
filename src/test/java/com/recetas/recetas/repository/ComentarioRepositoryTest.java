package com.recetas.recetas.repository;

import com.recetas.recetas.model.Comentario;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ComentarioRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    private Receta receta;
    private Usuario usuario;
    private Comentario comentario;
    
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
        
        // Crear comentario
        comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setComentario("Excelente receta!");
        comentario.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    void testSave() {
        // Act
        Comentario guardado = comentarioRepository.save(comentario);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardado.getId());
        assertEquals("Excelente receta!", guardado.getComentario());
    }
    
    @Test
    void testFindByRecetaIdOrderByFechaCreacionDesc() {
        // Arrange
        entityManager.persist(comentario);
        entityManager.flush();
        
        // Act
        List<Comentario> resultado = comentarioRepository.findByRecetaIdOrderByFechaCreacionDesc(receta.getId());
        
        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals("Excelente receta!", resultado.get(0).getComentario());
    }
    
    @Test
    void testCountByRecetaId() {
        // Arrange
        entityManager.persist(comentario);
        entityManager.flush();
        
        // Act
        long count = comentarioRepository.countByRecetaId(receta.getId());
        
        // Assert
        assertEquals(1L, count);
    }
    
    @Test
    void testFindByIdAndUsuarioId() {
        // Arrange
        entityManager.persist(comentario);
        entityManager.flush();
        
        // Act
        Optional<Comentario> resultado = comentarioRepository.findByIdAndUsuarioId(comentario.getId(), usuario.getId());
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(comentario.getId(), resultado.get().getId());
    }
}