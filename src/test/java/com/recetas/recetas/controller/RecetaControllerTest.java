package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaController.class)
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    private final Long VALID_ID = 10L;
    private final Receta MOCK_RECETA = new Receta();


    @Test
    @WithMockUser
    void testVerDetalleReceta_Exitoso() throws Exception {
        MOCK_RECETA.setNombre("Paella de Marisco");
        when(recetaService.obtenerRecetaPorId(VALID_ID)).thenReturn(Optional.of(MOCK_RECETA));

        mockMvc.perform(get("/recetas/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-receta"))
                .andExpect(model().attributeExists("receta"))
                .andExpect(model().attribute("receta", MOCK_RECETA));

        verify(recetaService, times(1)).obtenerRecetaPorId(VALID_ID);
    }
    

    @Test
    @WithMockUser
    void testVerDetalleReceta_IDInvalidoNegativo() throws Exception {
        Long INVALID_ID = -5L;

        mockMvc.perform(get("/recetas/{id}", INVALID_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar?error=id_invalido"));

        verify(recetaService, never()).obtenerRecetaPorId(anyLong());
    }

    @Test
    @WithMockUser
    void testVerDetalleReceta_IDInvalidoCero() throws Exception {
        Long INVALID_ID = 0L;

        mockMvc.perform(get("/recetas/{id}", INVALID_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar?error=id_invalido"));

        verify(recetaService, never()).obtenerRecetaPorId(anyLong());
    }


    @Test
    @WithMockUser
    void testVerDetalleReceta_RecetaNoEncontrada() throws Exception {
        Long NON_EXISTENT_ID = 999L;
        when(recetaService.obtenerRecetaPorId(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/recetas/{id}", NON_EXISTENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar?error=receta_no_encontrada"));
    }
    
    @Test
    @WithMockUser
    void testVerDetalleReceta_FalloDelServicio() throws Exception {
        when(recetaService.obtenerRecetaPorId(VALID_ID)).thenThrow(new IllegalStateException("Fallo de conexión a DB"));

        mockMvc.perform(get("/recetas/{id}", VALID_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar?error=receta_no_encontrada"));
    }

    
    @Test
    void testVerDetalleReceta_SinAutenticacion() throws Exception {
        when(recetaService.obtenerRecetaPorId(VALID_ID)).thenReturn(Optional.of(MOCK_RECETA));

        mockMvc.perform(get("/recetas/{id}", VALID_ID))
                .andExpect(status().isUnauthorized());
        
        verify(recetaService, never()).obtenerRecetaPorId(anyLong());
    }
}