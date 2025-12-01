package com.recetas.recetas.controller;

import com.recetas.recetas.dto.UsuarioRequest;
import com.recetas.recetas.dto.UsuarioResponse;
import com.recetas.recetas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de usuarios
 * API privada - Solo accesible para administradores
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Obtener todos los usuarios
     * GET /api/usuarios
     * Requiere rol ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     * Requiere rol ADMIN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioResponse usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Crear nuevo usuario
     * POST /api/usuarios
     * Requiere rol ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.crearUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear usuario: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar usuario existente
     * PUT /api/usuarios/{id}
     * Requiere rol ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.actualizarUsuario(id, request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar usuario
     * DELETE /api/usuarios/{id}
     * Requiere rol ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error al eliminar usuario: " + e.getMessage());
        }
    }
}

