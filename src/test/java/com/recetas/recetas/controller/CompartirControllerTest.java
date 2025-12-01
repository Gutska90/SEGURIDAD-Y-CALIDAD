package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.CompartirRequest;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.CompartirService;
import com.recetas.recetas.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CompartirController.class)
class CompartirControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompartirService compartirService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private SecurityUtil securityUtil;

    private final String USERNAME = "testuser";
    private final Long USER_ID = 1L;
    private final Long RECETA_ID = 42L;
    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setId(USER_ID);
        mockUsuario.setUsername(USERNAME);
        
        lenient().when(SecurityUtil.getCurrentUsername()).thenReturn(USERNAME);
    }
    
    @Test
    @WithMockUser(username = USERNAME, roles = {"USER"})
    void testCompartirReceta_Exitoso() throws Exception {
        CompartirRequest request = new CompartirRequest(RECETA_ID, "WhatsApp");
        when(usuarioRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockUsuario));
        
        mockMvc.perform(post("/api/compartir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Receta compartida exitosamente"));

        verify(compartirService, times(1)).registrarCompartido(
                RECETA_ID, USER_ID, "WhatsApp"
        );
    }
    
    @Test
    @WithMockUser(username = USERNAME, roles = {"USER"})
    void testCompartirReceta_UsuarioNoEncontrado() throws Exception {
        CompartirRequest request = new CompartirRequest(RECETA_ID, "Facebook");
        when(usuarioRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/compartir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuario no encontrado")));
        
        verify(compartirService, never()).registrarCompartido(anyLong(), anyLong(), anyString());
    }

    @Test
    void testCompartirReceta_NoAutenticado() throws Exception {
        CompartirRequest request = new CompartirRequest(RECETA_ID, "Twitter");
        
        mockMvc.perform(post("/api/compartir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(usuarioRepository, never()).findByUsername(anyString());
    }
    
    @Test
    @WithMockUser(username = USERNAME, roles = {"USER"})
    void testCompartirReceta_FalloDeServicio() throws Exception {
        CompartirRequest request = new CompartirRequest(RECETA_ID, "Email");
        when(usuarioRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockUsuario));
        doThrow(new RuntimeException("Receta no válida")).when(compartirService).registrarCompartido(anyLong(), anyLong(), anyString());

        mockMvc.perform(post("/api/compartir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Receta no válida")));
    }
    

    @Test
    @WithMockUser(username = USERNAME, roles = {"ADMIN"})
    void testObtenerLinkCompartir_Exitoso() throws Exception {
        Long testRecetaId = 10L;
        String mockBaseUrl = "http://testserver.com:8080";
        String generatedLink = mockBaseUrl + "/recetas/" + testRecetaId;
        
        when(compartirService.generarLinkCompartir(eq(testRecetaId), anyString())).thenReturn(generatedLink);

        mockMvc.perform(get("/api/compartir/receta/{recetaId}/link", testRecetaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value(generatedLink))
                .andExpect(jsonPath("$.facebook").isString())
                .andExpect(jsonPath("$.twitter").isString())
                .andExpect(jsonPath("$.whatsapp").isString());
        
        verify(compartirService, times(1)).generarLinkCompartir(eq(testRecetaId), anyString());
    }
    
    @Test
    void testObtenerLinkCompartir_AccesoDenegado() throws Exception {
        mockMvc.perform(get("/api/compartir/receta/{recetaId}/link", 10L))
                .andExpect(status().isUnauthorized());
        
        verify(compartirService, never()).generarLinkCompartir(anyLong(), anyString());
    }
}