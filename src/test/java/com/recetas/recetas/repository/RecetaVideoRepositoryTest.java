package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaVideo;
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
class RecetaVideoRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecetaVideoRepository recetaVideoRepository;

    private Receta receta;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setNombre("Video Receta Prueba");
        entityManager.persist(receta); 
        entityManager.flush();
    }

    private RecetaVideo crearVideo(Receta receta, LocalDateTime fecha) {
        RecetaVideo video = new RecetaVideo();
        video.setReceta(receta);
        video.setUrlVideo("http://video.test.com/clip" + fecha.getSecond());
        video.setFechaSubida(fecha);
        
        entityManager.persist(video);
        entityManager.flush(); 
        
        return video; 
    }

    @Test
    void testFindByRecetaIdOrderByFechaSubidaDesc() {
        crearVideo(receta, LocalDateTime.now().minusDays(5));
        RecetaVideo masReciente = crearVideo(receta, LocalDateTime.now().minusDays(1));
        
        Receta otraReceta = new Receta();
        otraReceta.setNombre("Otra Receta");
        entityManager.persist(otraReceta);
        crearVideo(otraReceta, LocalDateTime.now());
        entityManager.flush();

        List<RecetaVideo> resultado = recetaVideoRepository.findByRecetaIdOrderByFechaSubidaDesc(receta.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        
        assertEquals(masReciente.getId(), resultado.get(0).getId(), "El video más reciente debe estar primero");
    }

    @Test
    void testSave() {
        RecetaVideo nuevoVideo = new RecetaVideo();
        nuevoVideo.setReceta(receta);
        nuevoVideo.setUrlVideo("http://nuevo.com/video.mp4");
        nuevoVideo.setFechaSubida(LocalDateTime.now());
        
        RecetaVideo guardado = recetaVideoRepository.save(nuevoVideo);
        entityManager.flush();
        
        assertNotNull(guardado.getId());
        assertEquals(receta.getId(), guardado.getReceta().getId());
    }
    
    @Test
    void testDelete() {
        RecetaVideo videoAEliminar = crearVideo(receta, LocalDateTime.now());
        Long videoId = videoAEliminar.getId();
        
        recetaVideoRepository.deleteById(videoId);
        entityManager.flush();
        
        Optional<RecetaVideo> resultado = recetaVideoRepository.findById(videoId);
        assertFalse(resultado.isPresent(), "El video debe haber sido eliminado de la base de datos.");
    }
}