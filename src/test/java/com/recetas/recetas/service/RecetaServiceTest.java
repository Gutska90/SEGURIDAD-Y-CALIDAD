package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {
    
    @Mock
    private RecetaRepository recetaRepository;
    
    @InjectMocks
    private RecetaService recetaService;
    
    private Receta receta;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
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
    void testGuardarReceta() {
        // Arrange
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);
        
        // Act
        Receta resultado = recetaService.guardarReceta(receta);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Paella Valenciana", resultado.getNombre());
        verify(recetaRepository, times(1)).save(receta);
    }
    
    @Test
    void testObtenerRecetaPorId() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        
        // Act
        Optional<Receta> resultado = recetaService.obtenerRecetaPorId(1L);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Paella Valenciana", resultado.get().getNombre());
        verify(recetaRepository, times(1)).findById(1L);
    }
    
    @Test
    void testObtenerRecetaPorId_NoEncontrada() {
        // Arrange
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Receta> resultado = recetaService.obtenerRecetaPorId(999L);
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void testObtenerRecetasRecientes() {
        // Arrange
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findTop6ByOrderByFechaCreacionDesc()).thenReturn(recetas);
        
        // Act
        List<Receta> resultado = recetaService.obtenerRecetasRecientes();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository, times(1)).findTop6ByOrderByFechaCreacionDesc();
    }
    
    @Test
    void testBuscarPorNombre() {
        // Arrange
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByNombreContainingIgnoreCase("Paella")).thenReturn(recetas);
        
        // Act
        List<Receta> resultado = recetaService.buscarPorNombre("Paella");
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository, times(1)).findByNombreContainingIgnoreCase("Paella");
    }
}

