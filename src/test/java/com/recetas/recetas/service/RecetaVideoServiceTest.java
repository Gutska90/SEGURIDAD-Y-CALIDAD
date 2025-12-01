package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.RecetaVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RecetaVideoServiceTest {

    @Mock
    private RecetaVideoRepository recetaVideoRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ArchivoService archivoService;

    @InjectMocks
    private RecetaVideoService recetaVideoService;

    private Receta receta;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");

        mockFile = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                new byte[100]
        );
    }


    @Test
    void testSubirVideo_RecetaNoEncontrada() throws IOException {
        when(recetaRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaVideoService.subirVideo(99L, mockFile);
        });

        assertTrue(exception.getMessage().contains("Receta no encontrada"));
        verify(archivoService, never()).guardarVideo(any()); 
    }

    @Test
    void testSubirVideo_Exitoso() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarVideo(mockFile)).thenReturn("/uploads/videos/test_url.mp4");
        
        RecetaVideo videoGuardado = new RecetaVideo();
        when(recetaVideoRepository.save(any(RecetaVideo.class))).thenReturn(videoGuardado);

        RecetaVideo resultado = recetaVideoService.subirVideo(1L, mockFile);

        assertNotNull(resultado);
        verify(archivoService, times(1)).guardarVideo(mockFile);
        verify(recetaVideoRepository, times(1)).save(any(RecetaVideo.class));
    }


    @Test
    void testObtenerVideosPorReceta() {
        List<RecetaVideo> listaVideos = Arrays.asList(new RecetaVideo(), new RecetaVideo());
        when(recetaVideoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(listaVideos);

        List<RecetaVideo> resultado = recetaVideoService.obtenerVideosPorReceta(1L);

        assertEquals(2, resultado.size());
        verify(recetaVideoRepository, times(1)).findByRecetaIdOrderByFechaSubidaDesc(1L);
    }


    @Test
    void testEliminarVideo() {
        Long videoId = 5L;

        recetaVideoService.eliminarVideo(videoId);

        verify(recetaVideoRepository, times(1)).deleteById(videoId);
    }
}