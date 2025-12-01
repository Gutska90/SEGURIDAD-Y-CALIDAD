package com.recetas.recetas.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {
    
    @Test
    void testGetCurrentUsername_NoAuthentication() {
        // Limpiar contexto de seguridad
        SecurityContextHolder.clearContext();
        
        // Cuando no hay autenticación, debe retornar null
        String username = SecurityUtil.getCurrentUsername();
        assertNull(username, "Debe retornar null cuando no hay autenticación");
    }
    
    @Test
    void testGetCurrentUsername_WithAuthentication() {
        // Crear un UserDetails
        UserDetails userDetails = User.builder()
            .username("testuser")
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        
        // Crear autenticación
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            userDetails, null, Collections.emptyList()
        );
        
        // Establecer en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        try {
            // Probar el método
            String username = SecurityUtil.getCurrentUsername();
            assertEquals("testuser", username, "Debe retornar el username del usuario autenticado");
        } finally {
            // Limpiar contexto
            SecurityContextHolder.clearContext();
        }
    }
}

