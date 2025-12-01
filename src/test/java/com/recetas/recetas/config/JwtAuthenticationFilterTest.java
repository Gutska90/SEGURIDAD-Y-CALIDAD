package com.recetas.recetas.config;

import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private DetalleUserService userDetailsService;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        userDetails = User.builder()
            .username("testuser")
            .password("password")
            .authorities(Collections.emptyList())
            .build();
    }
    
    @Test
    void testDoFilterInternal_NoToken() throws Exception {
        // Cuando no hay token en el header
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // Ejecutar filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verificar que se continúa con la cadena
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }
    
    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Cuando hay un token inválido
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.extractUsername("invalid-token")).thenThrow(new RuntimeException("Token inválido"));
        
        // Ejecutar filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verificar que se continúa con la cadena (no debe fallar)
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        // Cuando hay un token válido
        String token = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        
        // Ejecutar filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verificar que se validó el token y se continuó con la cadena
        verify(jwtService, times(1)).extractUsername(token);
        verify(jwtService, times(1)).validateToken(token, userDetails);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}

