package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.repository.RecetaFotoRepository;
import com.recetas.recetas.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RecetaFotoServiceTest {

    @Mock
    private RecetaFotoRepository recetaFotoRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ArchivoService archivoService;

    @InjectMocks
    private RecetaFotoService recetaFotoService;

    private Receta receta;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");

        mockFile = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                new byte[100]
        );
    }


    @Test
    void testSubirFoto_RecetaNoEncontrada() throws IOException {
        when(recetaRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaFotoService.subirFoto(99L, mockFile);
        });

        assertTrue(exception.getMessage().contains("Receta no encontrada"));
        verify(archivoService, never()).guardarImagen(any());
    }

    @Test
    void testSubirFoto_EsPrincipal() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarImagen(mockFile)).thenReturn("/uploads/imagenes/test_url.jpg");
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(Collections.emptyList());
        
        RecetaFoto fotoGuardada = new RecetaFoto();
        when(recetaFotoRepository.save(any(RecetaFoto.class))).thenReturn(fotoGuardada);

        RecetaFoto resultado = recetaFotoService.subirFoto(1L, mockFile);

        assertTrue(resultado.getEsPrincipal(), "Debe ser marcada como principal si no hay fotos previas.");
        verify(archivoService, times(1)).guardarImagen(mockFile);
        verify(recetaFotoRepository, times(1)).save(any(RecetaFoto.class));
    }
    
    @Test
    void testSubirFoto_NoEsPrincipal() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarImagen(mockFile)).thenReturn("/uploads/imagenes/test_url.jpg");
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(
            Arrays.asList(new RecetaFoto())
        );
        
        RecetaFoto fotoGuardada = new RecetaFoto();
        when(recetaFotoRepository.save(any(RecetaFoto.class))).thenReturn(fotoGuardada);

        RecetaFoto resultado = recetaFotoService.subirFoto(1L, mockFile);

        assertFalse(resultado.getEsPrincipal(), "No debe ser marcada como principal si ya existen fotos.");
    }


    @Test
    void testObtenerFotosPorReceta() {
        List<RecetaFoto> listaFotos = Arrays.asList(new RecetaFoto(), new RecetaFoto());
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(listaFotos);

        List<RecetaFoto> resultado = recetaFotoService.obtenerFotosPorReceta(1L);

        assertEquals(2, resultado.size());
        verify(recetaFotoRepository, times(1)).findByRecetaIdOrderByFechaSubidaDesc(1L);
    }


    @Test
    void testEliminarFoto() {
        Long fotoId = 5L;

        recetaFotoService.eliminarFoto(fotoId);

        verify(recetaFotoRepository, times(1)).deleteById(fotoId);
    }


    @Test
    void testMarcarComoPrincipal_Exitoso() {
        RecetaFoto fotoPrincipalAntigua = new RecetaFoto();
        fotoPrincipalAntigua.setId(10L);
        fotoPrincipalAntigua.setEsPrincipal(true);

        RecetaFoto fotoNuevaPrincipal = new RecetaFoto();
        fotoNuevaPrincipal.setId(11L);
        fotoNuevaPrincipal.setEsPrincipal(false);

        List<RecetaFoto> fotos = Arrays.asList(fotoPrincipalAntigua, fotoNuevaPrincipal);

        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(fotos);
        when(recetaFotoRepository.findById(11L)).thenReturn(Optional.of(fotoNuevaPrincipal));

        recetaFotoService.marcarComoPrincipal(11L, 1L);

        verify(recetaFotoRepository, times(1)).saveAll(fotos);
        assertFalse(fotoPrincipalAntigua.getEsPrincipal(), "La antigua debe ser desmarcada");

        verify(recetaFotoRepository, times(1)).save(fotoNuevaPrincipal);
        assertTrue(fotoNuevaPrincipal.getEsPrincipal(), "La nueva debe ser marcada");
    }
    
    @Test
    void testMarcarComoPrincipal_FotoNoEncontrada() {
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(Collections.emptyList());
        when(recetaFotoRepository.findById(99L)).thenReturn(Optional.empty());

        recetaFotoService.marcarComoPrincipal(99L, 1L);

        verify(recetaFotoRepository, times(1)).findByRecetaIdOrderByFechaSubidaDesc(1L); 
        verify(recetaFotoRepository, never()).save(any(RecetaFoto.class));
    }
}