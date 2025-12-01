package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Modelo para valoraciones de recetas (1-5 estrellas)
 * Un usuario solo puede valorar una receta una vez
 */
@Entity
@Table(name = "valoraciones", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"receta_id", "usuario_id"}))
@Data
public class Valoracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private Integer puntuacion; // 1-5
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

