package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuscarController.class)
class BuscarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    private final Receta RECETA_TEST = new Receta();
    private final List<Receta> RESULTADOS_TEST = Collections.singletonList(RECETA_TEST);


    @Test
    void testBuscarPorNombre() throws Exception {
        String termino = "pasta";
        when(recetaService.buscarPorNombre(termino)).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar").param("nombre", termino))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attributeExists("resultados"))
                .andExpect(model().attribute("nombre", termino))
                .andExpect(model().attribute("resultados", RESULTADOS_TEST));

        verify(recetaService, times(1)).buscarPorNombre(termino);
        verify(recetaService, never()).buscarPorTipoCocina(anyString());
    }

    @Test
    void testBuscarPorTipoCocina() throws Exception {
        String termino = "Italiana";
        when(recetaService.buscarPorTipoCocina(termino)).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar").param("tipoCocina", termino))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("tipoCocina", termino))
                .andExpect(model().attribute("resultados", RESULTADOS_TEST));

        verify(recetaService, times(1)).buscarPorTipoCocina(termino);
        verify(recetaService, never()).buscarPorNombre(anyString());
    }

    @Test
    void testBuscarPorIngrediente() throws Exception {
        String termino = "tomate";
        when(recetaService.buscarPorIngrediente(termino)).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar").param("ingrediente", termino))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("ingrediente", termino));

        verify(recetaService, times(1)).buscarPorIngrediente(termino);
    }
    

    @Test
    void testSoloUsaPrimerParametro_Nombre() throws Exception {
        String nombre = "sopa";
        String tipoCocina = "francesa";
        when(recetaService.buscarPorNombre(nombre)).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar")
                .param("nombre", nombre)
                .param("tipoCocina", tipoCocina))
                .andExpect(status().isOk());

        verify(recetaService, times(1)).buscarPorNombre(nombre);
        verify(recetaService, never()).buscarPorTipoCocina(anyString());
        verify(recetaService, never()).buscarPorIngrediente(anyString());
    }
    

    @Test
    void testBuscarSinParametros_ObtieneTodo() throws Exception {
        List<Receta> todasRecetas = Arrays.asList(RECETA_TEST, new Receta());
        when(recetaService.obtenerTodasLasRecetas()).thenReturn(todasRecetas);

        mockMvc.perform(get("/buscar"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("resultados", todasRecetas));

        verify(recetaService, times(1)).obtenerTodasLasRecetas();
        verify(recetaService, never()).buscarPorNombre(anyString());
    }


    @Test
    void testEntradaConInjection_DebeSerRechazada() throws Exception {
        String payload = "pizza' UNION SELECT * FROM users --";
        
        mockMvc.perform(get("/buscar").param("nombre", payload))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("resultados", Collections.emptyList()));

        verify(recetaService, never()).buscarPorNombre(anyString());
    }
    
    @Test
    void testEntradaConXSS_DebeSerSanitizadaEnLlamadaAService() throws Exception {
        String xssInput = "<script>alert('XSS')</script>";
        String expectedSanitized = "scriptalert(XSS)/script";
        
        when(recetaService.buscarPorNombre(anyString())).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar").param("nombre", xssInput))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"));

        verify(recetaService, times(1)).buscarPorNombre(expectedSanitized);
        
        String expectedEscaped = "&lt;script&gt;alert('XSS')&lt;/script&gt;";
        mockMvc.perform(get("/buscar").param("nombre", xssInput))
                .andExpect(model().attribute("nombre", expectedEscaped));
    }
    
    @Test
    void testParametroVacioONulo_DebeIgnorarse() throws Exception {
        when(recetaService.obtenerTodasLasRecetas()).thenReturn(RESULTADOS_TEST);

        mockMvc.perform(get("/buscar").param("nombre", ""))
                .andExpect(status().isOk())
                .andExpect(model().attribute("resultados", RESULTADOS_TEST));

        verify(recetaService, times(1)).obtenerTodasLasRecetas();
        verify(recetaService, never()).buscarPorNombre(anyString());
    }
}