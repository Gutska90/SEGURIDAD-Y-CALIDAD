package com.recetas.recetas.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WebConfigTest {
    
    @Autowired
    private WebConfig webConfig;
    
    @Test
    void testWebConfigBean() {
        // Verificar que WebConfig está configurado como bean
        assertNotNull(webConfig, "WebConfig debe estar configurado como bean");
    }
    
    @Test
    void testAddResourceHandlers() {
        // Crear un registry mock para probar
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(null, null);
        
        // Llamar al método (no debería lanzar excepciones)
        assertDoesNotThrow(() -> {
            webConfig.addResourceHandlers(registry);
        }, "addResourceHandlers no debe lanzar excepciones");
    }
}

