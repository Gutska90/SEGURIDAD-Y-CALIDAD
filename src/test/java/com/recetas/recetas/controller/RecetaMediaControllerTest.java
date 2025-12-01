package com.recetas.recetas.controller;

import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.service.RecetaFotoService;
import com.recetas.recetas.service.RecetaVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaMediaController.class)
class RecetaMediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaFotoService recetaFotoService;

    @MockBean
    private RecetaVideoService recetaVideoService;

    private final Long RECETA_ID = 5L;
    private MockMultipartFile mockImage;
    private MockMultipartFile mockVideo;
    private RecetaFoto mockFoto;
    private RecetaVideo mockVideoEntity;

    @BeforeEach
    void setUp() {
        mockImage = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image data".getBytes());
        
        mockVideo = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "video data".getBytes());

        mockFoto = new RecetaFoto();
        mockFoto.setId(10L);
        mockFoto.setUrlFoto("/uploads/10.jpg");
        
        mockVideoEntity = new RecetaVideo();
        mockVideoEntity.setId(20L);
        mockVideoEntity.setUrlVideo("/uploads/20.mp4");
    }


    @Test
    @WithMockUser
    void testSubirFoto_Exitoso() throws Exception {
        when(recetaFotoService.subirFoto(eq(RECETA_ID), any(MockMultipartFile.class))).thenReturn(mockFoto);

        mockMvc.perform(multipart("/api/recetas/{id}/fotos", RECETA_ID)
                        .file(mockImage))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10L));

        verify(recetaFotoService, times(1)).subirFoto(eq(RECETA_ID), any());
    }
    
    @Test
    @WithMockUser
    void testSubirFoto_IOException() throws Exception {
        when(recetaFotoService.subirFoto(eq(RECETA_ID), any(MockMultipartFile.class)))
            .thenThrow(new IOException("Tamaño excedido"));

        mockMvc.perform(multipart("/api/recetas/{id}/fotos", RECETA_ID)
                        .file(mockImage))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tamaño excedido")));
    }

    @Test
    @WithMockUser
    void testObtenerFotos_Exitoso() throws Exception {
        List<RecetaFoto> fotos = Collections.singletonList(mockFoto);
        when(recetaFotoService.obtenerFotosPorReceta(RECETA_ID)).thenReturn(fotos);

        mockMvc.perform(get("/api/recetas/{id}/fotos", RECETA_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
        
        verify(recetaFotoService, times(1)).obtenerFotosPorReceta(RECETA_ID);
    }
    
    @Test
    @WithMockUser
    void testEliminarFoto_Exitoso() throws Exception {
        Long FOTO_ID = 10L;
        doNothing().when(recetaFotoService).eliminarFoto(FOTO_ID);

        mockMvc.perform(delete("/api/recetas/{id}/fotos/{fotoId}", RECETA_ID, FOTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Foto eliminada exitosamente"));
        
        verify(recetaFotoService, times(1)).eliminarFoto(FOTO_ID);
    }


    @Test
    @WithMockUser
    void testSubirVideo_Exitoso() throws Exception {
        when(recetaVideoService.subirVideo(eq(RECETA_ID), any(MockMultipartFile.class))).thenReturn(mockVideoEntity);

        mockMvc.perform(multipart("/api/recetas/{id}/videos", RECETA_ID)
                        .file(mockVideo))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(20L));

        verify(recetaVideoService, times(1)).subirVideo(eq(RECETA_ID), any());
    }

    @Test
    @WithMockUser
    void testObtenerVideos_Exitoso() throws Exception {
        List<RecetaVideo> videos = Collections.singletonList(mockVideoEntity);
        when(recetaVideoService.obtenerVideosPorReceta(RECETA_ID)).thenReturn(videos);

        mockMvc.perform(get("/api/recetas/{id}/videos", RECETA_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L));
        
        verify(recetaVideoService, times(1)).obtenerVideosPorReceta(RECETA_ID);
    }

    @Test
    @WithMockUser
    void testEliminarVideo_Exitoso() throws Exception {
        Long VIDEO_ID = 20L;
        doNothing().when(recetaVideoService).eliminarVideo(VIDEO_ID);

        mockMvc.perform(delete("/api/recetas/{id}/videos/{videoId}", RECETA_ID, VIDEO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Video eliminado exitosamente"));
        
        verify(recetaVideoService, times(1)).eliminarVideo(VIDEO_ID);
    }


    @Test
    void testSubirFoto_NoAutenticado_AccesoDenegado() throws Exception {

        mockMvc.perform(multipart("/api/recetas/{id}/fotos", RECETA_ID)
                        .file(mockImage))
                .andExpect(status().isUnauthorized());
        
        verify(recetaFotoService, never()).subirFoto(anyLong(), any());
    }
    
    @Test
    void testObtenerVideos_NoAutenticado_AccesoDenegado() throws Exception {
        mockMvc.perform(get("/api/recetas/{id}/videos", RECETA_ID))
                .andExpect(status().isUnauthorized());
        
        verify(recetaVideoService, never()).obtenerVideosPorReceta(anyLong());
    }
}