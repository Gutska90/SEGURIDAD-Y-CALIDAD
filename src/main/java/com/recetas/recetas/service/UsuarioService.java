package com.recetas.recetas.service;

import com.recetas.recetas.dto.UsuarioRequest;
import com.recetas.recetas.dto.UsuarioResponse;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios
 * Solo accesible para administradores
 */
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Obtener todos los usuarios
     */
    public List<UsuarioResponse> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener usuario por ID
     */
    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirAUsuarioResponse(usuario);
    }
    
    /**
     * Crear nuevo usuario
     */
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        // Verificar si el username ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }
        
        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está en uso");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        
        // Encriptar contraseña si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        
        usuario.setEnabledBoolean(request.getEnabled() != null ? request.getEnabled() : true);
        
        // Asignar rol
        String roleName = request.getRole() != null ? request.getRole() : "ROLE_USER";
        Role role = roleRepository.findByNombre(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        usuario.setRoles(roles);
        
        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirAUsuarioResponse(usuarioGuardado);
    }
    
    /**
     * Actualizar usuario existente
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Verificar si el username ya existe (y no es el mismo usuario)
        usuarioRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    if (!Objects.equals(u.getId(), id)) {
                        throw new RuntimeException("El username ya está en uso");
                    }
                });
        
        // Verificar si el email ya existe (y no es el mismo usuario)
        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    if (!Objects.equals(u.getId(), id)) {
                        throw new RuntimeException("El email ya está en uso");
                    }
                });
        
        // Actualizar campos
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        
        // Actualizar contraseña solo si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getEnabled() != null) {
            usuario.setEnabledBoolean(request.getEnabled());
        }
        
        // Actualizar rol si se proporciona
        if (request.getRole() != null) {
            Role role = roleRepository.findByNombre(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRole()));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            usuario.setRoles(roles);
        }
        
        // Guardar cambios
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAUsuarioResponse(usuarioActualizado);
    }
    
    /**
     * Eliminar usuario
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Convertir Usuario a UsuarioResponse
     */
    private UsuarioResponse convertirAUsuarioResponse(Usuario usuario) {
        Set<String> roles = usuario.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());
        
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.isEnabled(),
                roles
        );
    }
}

