package com.recetas.recetas.repository;

import com.recetas.recetas.model.Anuncio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AnuncioRepositoryTest {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private AnuncioRepository anuncioRepository;
    
    private Anuncio anuncio;
    
    @BeforeEach
    void setUp() {
        anuncio = new Anuncio();
        anuncio.setEmpresa("Test Company");
        anuncio.setTitulo("Test Ad");
        anuncio.setDescripcion("Test Description");
        anuncio.setUrlDestino("https://example.com");
        anuncio.setImagenUrl("https://example.com/image.jpg");
        anuncio.setActivo(1);
    }
    
    @Test
    void testSave() {
        // Act
        Anuncio guardado = anuncioRepository.save(anuncio);
        entityManager.flush();
        
        // Assert
        assertNotNull(guardado.getId());
        assertEquals("Test Company", guardado.getEmpresa());
        assertEquals("Test Ad", guardado.getTitulo());
    }
    
    @Test
    void testFindByActivoTrue() {
        // Arrange
        Anuncio anuncioInactivo = new Anuncio();
        anuncioInactivo.setEmpresa("Inactive Company");
        anuncioInactivo.setTitulo("Inactive Ad");
        anuncioInactivo.setActivo(0);
        
        entityManager.persist(anuncio);
        entityManager.persist(anuncioInactivo);
        entityManager.flush();
        
        // Act
        List<Anuncio> activos = anuncioRepository.findByActivoTrue();
        
        // Assert
        assertFalse(activos.isEmpty());
        assertTrue(activos.stream().anyMatch(a -> a.getEmpresa().equals("Test Company")));
        assertTrue(activos.stream().noneMatch(a -> a.getEmpresa().equals("Inactive Company")));
    }
    
    @Test
    void testFindById() {
        // Arrange
        entityManager.persist(anuncio);
        entityManager.flush();
        Long id = anuncio.getId();
        
        // Act
        Anuncio encontrado = anuncioRepository.findById(id).orElse(null);
        
        // Assert
        assertNotNull(encontrado);
        assertEquals("Test Company", encontrado.getEmpresa());
    }
}

