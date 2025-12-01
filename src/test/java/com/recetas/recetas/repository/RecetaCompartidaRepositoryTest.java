package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaCompartida;
import com.recetas.recetas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RecetaCompartidaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecetaCompartidaRepository recetaCompartidaRepository;

    private Receta receta1;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsername("compartidor");
        usuario.setEmail("compartidor@test.com");
        usuario.setPassword("pass");
        entityManager.persist(usuario);

        receta1 = new Receta();
        receta1.setNombre("Tarta de Manzana");
        entityManager.persist(receta1);
        
        entityManager.flush();
    }

    private RecetaCompartida crearRecetaCompartida(Receta receta, LocalDateTime fecha) {
        RecetaCompartida compartida = new RecetaCompartida();
        compartida.setReceta(receta);
        compartida.setUsuario(usuario);
        
        compartida.setPlataforma("facebook"); 
        
        compartida.setFechaCompartido(fecha);
        
        entityManager.persist(compartida);
        entityManager.flush();
        
        return compartida;
    }

    @Test
    void testFindByRecetaIdOrderByFechaCompartidoDesc() {
        crearRecetaCompartida(receta1, LocalDateTime.now().minusDays(2));
        RecetaCompartida masReciente = crearRecetaCompartida(receta1, LocalDateTime.now().minusDays(1));
        entityManager.flush();

        List<RecetaCompartida> resultado = recetaCompartidaRepository.findByRecetaIdOrderByFechaCompartidoDesc(receta1.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        assertEquals(masReciente.getId(), resultado.get(0).getId(), "La más reciente debe ser la primera");
    }

    @Test
    void testCountByRecetaId() {
        crearRecetaCompartida(receta1, LocalDateTime.now());
        crearRecetaCompartida(receta1, LocalDateTime.now().minusHours(1));
        
        Receta receta2 = new Receta();
        receta2.setNombre("Sopa");
        entityManager.persist(receta2);
        entityManager.flush();
        
        long count = recetaCompartidaRepository.countByRecetaId(receta1.getId());

        assertEquals(2L, count);
        assertEquals(0L, recetaCompartidaRepository.countByRecetaId(receta2.getId()));
    }
    
    @Test
    void testSave() {
        RecetaCompartida nueva = new RecetaCompartida();
        nueva.setReceta(receta1);
        nueva.setUsuario(usuario);
        nueva.setFechaCompartido(LocalDateTime.now());
        
        RecetaCompartida guardada = recetaCompartidaRepository.save(nueva);
        entityManager.flush();
        
        assertNotNull(guardada.getId());
        assertEquals(receta1.getId(), guardada.getReceta().getId());
    }
}