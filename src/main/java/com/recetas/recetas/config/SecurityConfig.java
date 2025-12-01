package com.recetas.recetas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Configuración de seguridad de Spring Security
 * Implementa protecciones OWASP Top 10:
 * - A01:2021 Broken Access Control
 * - A02:2021 Cryptographic Failures
 * - A05:2021 Security Misconfiguration
 * - A07:2021 Identification and Authentication Failures
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para APIs REST (JWT es stateless)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            
            // Configuración de sesión para APIs REST (stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Agregar filtro JWT antes del filtro de autenticación de usuario
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Configuración de autorización - OWASP A01: Broken Access Control
            .authorizeHttpRequests(authorize -> authorize
                // APIs públicas
                .requestMatchers("/api/auth/login", "/api/auth/registro").permitAll()
                // Páginas web públicas
                .requestMatchers("/", "/inicio", "/buscar", "/css/**", "/js/**", "/images/**", "/error", "/login").permitAll()
                // APIs privadas (requieren JWT)
                .requestMatchers("/api/recetas/**").authenticated()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/comentarios/**").authenticated()
                .requestMatchers("/api/valoraciones/**").authenticated()
                .requestMatchers("/api/compartir/**").authenticated()
                .requestMatchers("/api/media/**").authenticated()
                // Páginas web que requieren autenticación
                .requestMatchers("/recetas/**").authenticated()
                .anyRequest().authenticated()
            )
            
            // Configuración de login para páginas web - OWASP A07: Identification and Authentication Failures
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Configuración de logout para páginas web
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/inicio")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Manejo de excepciones
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso-denegado")
            )
            
            // Headers de seguridad - OWASP A05: Security Misconfiguration
            .headers(headers -> headers
                // Protección contra Clickjacking
                .frameOptions(frame -> frame.deny())
                
                // Protección XSS - OWASP A03: Injection
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
                
                // Content Security Policy - OWASP A03: Injection
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:; " +
                        "font-src 'self' data:; " +
                        "frame-ancestors 'none';")
                )
                
                // HTTP Strict Transport Security (HSTS)
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
                
                // Referrer Policy
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            );

        return http.build();
    }

    /**
     * Codificador de contraseñas usando BCrypt
     * OWASP A02:2021 - Cryptographic Failures
     * BCrypt es resistente a ataques de fuerza bruta
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Aumentado a 12 rounds para mayor seguridad
    }
    
    /**
     * Bean para AuthenticationManager necesario para autenticación en APIs REST
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
