package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recetas")
@Data
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String tipoCocina;
    
    private String paisOrigen;
    
    private String dificultad;
    
    private Integer tiempoCoccion;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(columnDefinition = "TEXT")
    private String ingredientes;
    
    @Column(columnDefinition = "TEXT")
    private String instrucciones;
    
    private String imagenUrl;
    
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private Integer popularidad = 0;
}
