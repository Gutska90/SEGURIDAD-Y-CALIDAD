package com.recetas.recetas.controller;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ErrorController.class)
class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testHandleError_404NotFound() throws Exception {
        final String URI_FALLIDA = "/ruta-no-existente";
        final String STATUS_CODE = "404";
        
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, URI_FALLIDA))
                
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", STATUS_CODE))
                .andExpect(model().attribute("errorMessage", "Página no encontrada"));
    }

    @Test
    void testHandleError_403Forbidden() throws Exception {
        final String STATUS_CODE = "403";

        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", STATUS_CODE))
                .andExpect(model().attribute("errorMessage", "Acceso denegado"));
    }

    @Test
    void testHandleError_500InternalServerError() throws Exception {
        final String STATUS_CODE = "500";
        final String URI_FALLIDA = "/api/ejemplo";
        
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, URI_FALLIDA))
                
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", STATUS_CODE))
                .andExpect(model().attribute("errorMessage", "Error interno del servidor"));
    }
    
    @Test
    void testHandleError_SinStatus() throws Exception {
        mockMvc.perform(get("/error"))
                
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "500"))
                .andExpect(model().attribute("errorMessage", "Ha ocurrido un error"));
    }


    @Test
    void testAccessDenied() throws Exception {
        mockMvc.perform(get("/acceso-denegado"))
                
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "No tienes permisos para acceder a este recurso"));
    }

    
    @Test
    void testHandleException_ManejoGenerico() {
        ErrorController controller = new ErrorController();
        Exception testException = new RuntimeException("Error en la DB");
        org.springframework.ui.ConcurrentModel model = new org.springframework.ui.ConcurrentModel();
        
        String viewName = controller.handleException(testException, model);
        
        assertEquals("error", viewName);
        assertEquals("500", model.getAttribute("errorCode"));
        assertEquals("Ha ocurrido un error inesperado. Por favor, intente nuevamente.", 
                     model.getAttribute("errorMessage"));
        
    }
}