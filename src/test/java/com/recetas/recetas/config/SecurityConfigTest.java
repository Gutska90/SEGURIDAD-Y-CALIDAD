package com.recetas.recetas.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void testPasswordEncoder() {
        // Verificar que el PasswordEncoder está configurado
        assertNotNull(passwordEncoder, "PasswordEncoder debe estar configurado");
        
        // Verificar que puede encriptar contraseñas
        String password = "testPassword123";
        String encoded = passwordEncoder.encode(password);
        
        assertNotNull(encoded, "La contraseña encriptada no debe ser nula");
        assertNotEquals(password, encoded, "La contraseña encriptada debe ser diferente a la original");
        
        // Verificar que puede validar contraseñas
        assertTrue(passwordEncoder.matches(password, encoded), 
            "El PasswordEncoder debe poder validar contraseñas correctamente");
    }
}

