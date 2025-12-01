package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaCompartida;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RecetaCompartidaRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompartirServiceTest {
    
    @Mock
    private RecetaCompartidaRepository recetaCompartidaRepository;
    
    @Mock
    private RecetaRepository recetaRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private CompartirService compartirService;
    
    private Receta receta;
    private Usuario usuario;
    private RecetaCompartida recetaCompartida;
    
    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Paella");
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        
        recetaCompartida = new RecetaCompartida();
        recetaCompartida.setId(1L);
        recetaCompartida.setReceta(receta);
        recetaCompartida.setUsuario(usuario);
        recetaCompartida.setPlataforma("facebook");
    }
    
    @Test
    void testRegistrarCompartido() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(recetaCompartidaRepository.save(any(RecetaCompartida.class))).thenReturn(recetaCompartida);
        
        // Act
        RecetaCompartida resultado = compartirService.registrarCompartido(1L, 1L, "facebook");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("facebook", resultado.getPlataforma());
        verify(recetaCompartidaRepository, times(1)).save(any(RecetaCompartida.class));
    }
    
    @Test
    void testGenerarLinkCompartir() {
        // Act
        String link = compartirService.generarLinkCompartir(1L, "http://localhost:8080");
        
        // Assert
        assertNotNull(link);
        assertEquals("http://localhost:8080/recetas/1", link);
    }
    
    @Test
    void testContarCompartidosPorReceta() {
        // Arrange
        when(recetaCompartidaRepository.countByRecetaId(1L)).thenReturn(5L);
        
        // Act
        long resultado = compartirService.contarCompartidosPorReceta(1L);
        
        // Assert
        assertEquals(5L, resultado);
        verify(recetaCompartidaRepository, times(1)).countByRecetaId(1L);
    }
}

