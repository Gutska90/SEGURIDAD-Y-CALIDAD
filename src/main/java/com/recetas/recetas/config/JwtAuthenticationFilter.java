package com.recetas.recetas.config;

import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para autenticación JWT
 * Intercepta las peticiones y valida el token JWT en el header Authorization
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private DetalleUserService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // Verificar si el header Authorization existe y comienza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraer el token (remover "Bearer " del inicio)
        jwt = authHeader.substring(7);
        
        try {
            // Extraer el username del token
            username = jwtService.extractUsername(jwt);
            
            // Si el username existe y no hay autenticación en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cargar los detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validar el token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si hay algún error al procesar el token, continuar sin autenticación
            logger.error("Error al procesar token JWT", e);
        }
        
        filterChain.doFilter(request, response);
    }
}

