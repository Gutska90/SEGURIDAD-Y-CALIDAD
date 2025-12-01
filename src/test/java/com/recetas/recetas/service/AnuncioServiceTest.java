package com.recetas.recetas.service;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.repository.AnuncioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnuncioServiceTest {
    
    @Mock
    private AnuncioRepository anuncioRepository;
    
    @InjectMocks
    private AnuncioService anuncioService;
    
    private Anuncio anuncio;
    
    @BeforeEach
    void setUp() {
        anuncio = new Anuncio();
        anuncio.setId(1L);
        anuncio.setEmpresa("Test Company");
        anuncio.setTitulo("Test Ad");
        anuncio.setActivo(1);
    }
    
    @Test
    void testObtenerAnunciosActivos() {
        // Arrange
        List<Anuncio> anuncios = Arrays.asList(anuncio);
        when(anuncioRepository.findByActivoTrue()).thenReturn(anuncios);
        
        // Act
        List<Anuncio> resultado = anuncioService.obtenerAnunciosActivos();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anuncioRepository, times(1)).findByActivoTrue();
    }
}

