package com.recetas.recetas.dto;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para respuesta de usuario
 */
public class UsuarioResponse {
    
    private Long id;
    private String nombreCompleto;
    private String username;
    private String email;
    private Boolean enabled;
    private Set<String> roles;
    
    public UsuarioResponse() {
    }
    
    public UsuarioResponse(Long id, String nombreCompleto, String username, String email, Boolean enabled, Set<String> roles) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

