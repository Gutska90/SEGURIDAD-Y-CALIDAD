package com.recetas.recetas.service;

import com.recetas.recetas.model.Comentario;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
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
class ComentarioServiceTest {
    
    @Mock
    private ComentarioRepository comentarioRepository;
    
    @Mock
    private RecetaRepository recetaRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private ComentarioService comentarioService;
    
    private Receta receta;
    private Usuario usuario;
    private Comentario comentario;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setNombreCompleto("Test User");
        
        comentario = new Comentario();
        comentario.setId(1L);
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setComentario("Excelente receta!");
        comentario.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    void testCrearComentario() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        
        // Act
        Comentario resultado = comentarioService.crearComentario(1L, 1L, "Excelente receta!");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Excelente receta!", resultado.getComentario());
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }
    
    @Test
    void testCrearComentario_RecetaNoEncontrada() {
        // Arrange
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            comentarioService.crearComentario(999L, 1L, "Comentario"));
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }
    
    @Test
    void testObtenerComentariosPorReceta() {
        // Arrange
        List<Comentario> comentarios = Arrays.asList(comentario);
        when(comentarioRepository.findByRecetaIdOrderByFechaCreacionDesc(1L)).thenReturn(comentarios);
        
        // Act
        var resultado = comentarioService.obtenerComentariosPorReceta(1L);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(comentarioRepository, times(1)).findByRecetaIdOrderByFechaCreacionDesc(1L);
    }
    
    @Test
    void testEliminarComentario() {
        // Arrange
        when(comentarioRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(comentario));
        doNothing().when(comentarioRepository).delete(comentario);
        
        // Act
        comentarioService.eliminarComentario(1L, 1L);
        
        // Assert
        verify(comentarioRepository, times(1)).delete(comentario);
    }
    
    @Test
    void testContarComentariosPorReceta() {
        // Arrange
        when(comentarioRepository.countByRecetaId(1L)).thenReturn(5L);
        
        // Act
        long resultado = comentarioService.contarComentariosPorReceta(1L);
        
        // Assert
        assertEquals(5L, resultado);
        verify(comentarioRepository, times(1)).countByRecetaId(1L);
    }
}

