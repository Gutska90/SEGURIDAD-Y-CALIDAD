package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RecetaRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    private Receta receta;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setNombre("Paella Valenciana");
        receta.setTipoCocina("Mediterránea");
        receta.setPaisOrigen("España");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(60);
        receta.setIngredientes("Arroz, pollo, mariscos");
        receta.setInstrucciones("1. Sofreír el pollo\n2. Agregar arroz");
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setPopularidad(95);
    }
    
    @Test
    void testSave() {
        // Act
        Receta guardada = recetaRepository.save(receta);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardada.getId());
        assertEquals("Paella Valenciana", guardada.getNombre());
    }
    
    @Test
    void testFindById() {
        // Arrange
        entityManager.persist(receta);
        entityManager.flush();
        
        // Act
        Optional<Receta> resultado = recetaRepository.findById(receta.getId());
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Paella Valenciana", resultado.get().getNombre());
    }
    
    @Test
    void testFindByNombreContainingIgnoreCase() {
        // Arrange
        entityManager.persist(receta);
        entityManager.flush();
        
        // Act
        List<Receta> resultado = recetaRepository.findByNombreContainingIgnoreCase("paella");
        
        // Assert
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().anyMatch(r -> r.getNombre().contains("Paella")));
    }
    
    @Test
    void testFindTop6ByOrderByFechaCreacionDesc() {
        // Arrange
        entityManager.persist(receta);
        
        // Crear otra receta más antigua
        Receta receta2 = new Receta();
        receta2.setNombre("Gazpacho");
        receta2.setTipoCocina("Mediterránea");
        receta2.setPaisOrigen("España");
        receta2.setDificultad("Fácil");
        receta2.setTiempoCoccion(15);
        receta2.setIngredientes("Tomate, pepino");
        receta2.setInstrucciones("Mezclar todo");
        receta2.setFechaCreacion(LocalDateTime.now().minusDays(1));
        entityManager.persist(receta2);
        entityManager.flush();
        
        // Act
        List<Receta> resultado = recetaRepository.findTop6ByOrderByFechaCreacionDesc();
        
        // Assert
        assertFalse(resultado.isEmpty());
        // La más reciente debe ser la primera
        assertEquals("Paella Valenciana", resultado.get(0).getNombre());
    }
}