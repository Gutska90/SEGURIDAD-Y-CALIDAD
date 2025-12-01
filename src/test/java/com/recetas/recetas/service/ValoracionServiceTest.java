package com.recetas.recetas.service;

import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.model.Valoracion;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.repository.ValoracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionServiceTest {
    
    @Mock
    private ValoracionRepository valoracionRepository;
    
    @Mock
    private RecetaRepository recetaRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private ValoracionService valoracionService;
    
    private Receta receta;
    private Usuario usuario;
    private Valoracion valoracion;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        
        valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setReceta(receta);
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(5);
        valoracion.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    void testCrearOActualizarValoracion_Nueva() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.empty());
        when(valoracionRepository.save(any(Valoracion.class))).thenReturn(valoracion);
        
        // Act
        Valoracion resultado = valoracionService.crearOActualizarValoracion(1L, 1L, 5);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(5, resultado.getPuntuacion());
        verify(valoracionRepository, times(1)).save(any(Valoracion.class));
    }
    
    @Test
    void testCrearOActualizarValoracion_Actualizar() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(valoracion));
        when(valoracionRepository.save(any(Valoracion.class))).thenReturn(valoracion);
        
        // Act
        Valoracion resultado = valoracionService.crearOActualizarValoracion(1L, 1L, 4);
        
        // Assert
        assertNotNull(resultado);
        verify(valoracionRepository, times(1)).save(valoracion);
    }
    
    @Test
    void testObtenerValoracionesPorReceta() {
        // Arrange
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(4.5);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(10L);
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(valoracion));
        
        // Act
        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, 1L);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(4.5, resultado.getPromedio());
        assertEquals(10L, resultado.getTotalValoraciones());
        assertEquals(5, resultado.getMiValoracion());
    }
}

