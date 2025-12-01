package com.recetas.recetas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear/actualizar usuarios
 */
public class UsuarioRequest {
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre completo debe tener entre 3 y 200 caracteres")
    private String nombreCompleto;
    
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String password; // Opcional para actualización
    
    private Boolean enabled = true;
    
    private String role; // ROLE_USER o ROLE_ADMIN
    
    public UsuarioRequest() {
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}

