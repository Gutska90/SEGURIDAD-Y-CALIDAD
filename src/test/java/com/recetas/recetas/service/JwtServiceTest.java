package com.recetas.recetas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    
    private JwtService jwtService;
    
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Configurar secret y expiration usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secret", 
            "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
        
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }
    
    @Test
    void testGenerateToken() {
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    
    @Test
    void testExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Act
        String username = jwtService.extractUsername(token);
        
        // Assert
        assertEquals("testuser", username);
    }
    
    @Test
    void testValidateToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Act
        Boolean isValid = jwtService.validateToken(token, userDetails);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testValidateToken_InvalidUser() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("otheruser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        // Act
        Boolean isValid = jwtService.validateToken(token, otherUser);
        
        // Assert
        assertFalse(isValid);
    }
}

