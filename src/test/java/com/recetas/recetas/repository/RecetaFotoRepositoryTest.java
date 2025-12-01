package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaFoto;
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
class RecetaFotoRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecetaFotoRepository recetaFotoRepository;

    private Receta receta;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setNombre("Ensalada César");
        entityManager.persist(receta);
        entityManager.flush();
    }

    private RecetaFoto crearFoto(Receta receta, LocalDateTime fecha, boolean esPrincipal) {
        RecetaFoto foto = new RecetaFoto();
        foto.setReceta(receta);
        foto.setUrlFoto("http://test.com/img" + fecha.getSecond());
        foto.setEsPrincipal(esPrincipal);
        foto.setFechaSubida(fecha);
        
        entityManager.persist(foto); 
        
        entityManager.flush(); 
        
        return foto; 
    }

    @Test
    void testFindByRecetaIdOrderByFechaSubidaDesc() {
        crearFoto(receta, LocalDateTime.now().minusDays(5), false);
        RecetaFoto masReciente = crearFoto(receta, LocalDateTime.now().minusDays(1), true);
        entityManager.flush();

        List<RecetaFoto> resultado = recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(receta.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        assertEquals(masReciente.getId(), resultado.get(0).getId(), "La foto más reciente debe estar primero");
    }

    @Test
    void testFindByRecetaIdAndEsPrincipalTrue_Encontrada() {
        crearFoto(receta, LocalDateTime.now().minusHours(2), false);
        RecetaFoto principal = crearFoto(receta, LocalDateTime.now().minusHours(1), true);
        entityManager.flush();

        Optional<RecetaFoto> resultado = recetaFotoRepository.findByRecetaIdAndEsPrincipalTrue(receta.getId());

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().getEsPrincipal());
        assertEquals(principal.getId(), resultado.get().getId());
    }

    @Test
    void testFindByRecetaIdAndEsPrincipalTrue_NoEncontrada() {
        crearFoto(receta, LocalDateTime.now(), false);
        entityManager.flush();

        Optional<RecetaFoto> resultado = recetaFotoRepository.findByRecetaIdAndEsPrincipalTrue(receta.getId());

        assertFalse(resultado.isPresent());
    }
    
    @Test
    void testSave() {
        RecetaFoto nuevaFoto = new RecetaFoto();
        nuevaFoto.setReceta(receta);
        nuevaFoto.setUrlFoto("http://nueva.com/img.jpg");
        nuevaFoto.setEsPrincipal(true);
        nuevaFoto.setFechaSubida(LocalDateTime.now());
        
        RecetaFoto guardada = recetaFotoRepository.save(nuevaFoto);
        entityManager.flush();
        
        assertNotNull(guardada.getId());
        assertTrue(guardada.getEsPrincipal());
    }
}