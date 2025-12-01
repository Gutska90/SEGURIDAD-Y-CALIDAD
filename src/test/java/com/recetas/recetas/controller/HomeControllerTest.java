package com.recetas.recetas.controller;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.AnuncioService;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    @MockBean
    private AnuncioService anuncioService;


    @Test
    void testInicio_CargaDatosYRetornaVista() throws Exception {
        Receta receta1 = new Receta();
        receta1.setNombre("Reciente");
        Receta receta2 = new Receta();
        receta2.setNombre("Popular");
        
        List<Receta> recientes = Arrays.asList(receta1);
        List<Receta> populares = Arrays.asList(receta2);
        
        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo("Anuncio Test");
        List<Anuncio> anuncios = Arrays.asList(anuncio);

        when(recetaService.obtenerRecetasRecientes()).thenReturn(recientes);
        when(recetaService.obtenerRecetasPopulares()).thenReturn(populares);
        when(anuncioService.obtenerAnunciosActivos()).thenReturn(anuncios);

        
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attribute("recetasRecientes", recientes))
                .andExpect(model().attribute("recetasPopulares", populares))
                .andExpect(model().attribute("anuncios", anuncios));

        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"));
    }

    @Test
    void testInicio_SinDatos() throws Exception {
        when(recetaService.obtenerRecetasRecientes()).thenReturn(List.of());
        when(recetaService.obtenerRecetasPopulares()).thenReturn(List.of());
        when(anuncioService.obtenerAnunciosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attributeExists("recetasRecientes"))
                .andExpect(model().attribute("recetasRecientes", List.of()));
    }


    @Test
    void testLogin_RetornaVistaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}