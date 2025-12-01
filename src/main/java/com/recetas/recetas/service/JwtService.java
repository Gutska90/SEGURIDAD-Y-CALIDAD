package com.recetas.recetas.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para generar y validar tokens JWT
 */
@Service
public class JwtService {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long expiration;
    
    /**
     * Genera un token JWT para un usuario
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Crea un token JWT con los claims especificados
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Extrae el username del token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Verifica si el token ha expirado
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Valida un token JWT
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Obtiene la clave de firma para JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

