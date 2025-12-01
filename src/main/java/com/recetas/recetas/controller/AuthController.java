package com.recetas.recetas.controller;

import com.recetas.recetas.dto.LoginRequest;
import com.recetas.recetas.dto.LoginResponse;
import com.recetas.recetas.dto.RegistroRequest;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Controlador REST para autenticación
 * APIs públicas: /api/auth/login y /api/auth/registro
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private DetalleUserService userDetailsService;
    
    /**
     * API pública para login
     * POST /api/auth/login
     * Retorna un token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            
            // Generar token JWT
            String token = jwtService.generateToken(userDetails);
            
            // Retornar respuesta con token
            LoginResponse response = new LoginResponse(token, loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación: " + e.getMessage());
        }
    }
    
    /**
     * API pública para registro de usuarios
     * POST /api/auth/registro
     * Registra un nuevo usuario con contraseña encriptada
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest registroRequest) {
        try {
            // Verificar si el username ya existe
            if (usuarioRepository.findByUsername(registroRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El username ya está en uso");
            }
            
            // Verificar si el email ya existe
            if (usuarioRepository.findByEmail(registroRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El email ya está en uso");
            }
            
            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombreCompleto(registroRequest.getNombreCompleto());
            usuario.setUsername(registroRequest.getUsername());
            usuario.setEmail(registroRequest.getEmail());
            usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
            usuario.setEnabledBoolean(true);
            
            // Asignar rol por defecto (ROLE_USER)
            Role roleUser = roleRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(roleUser);
            usuario.setRoles(roles);
            
            // Guardar usuario
            usuarioRepository.save(usuario);
            
            // Generar token JWT para el nuevo usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            // Retornar respuesta con token
            LoginResponse response = new LoginResponse(token, usuario.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }
}

