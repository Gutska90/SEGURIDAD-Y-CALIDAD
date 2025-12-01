package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Modelo para registrar compartidos de recetas
 */
@Entity
@Table(name = "receta_compartidas")
@Data
public class RecetaCompartida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "plataforma", length = 50)
    private String plataforma; // facebook, twitter, whatsapp, web, etc.
    
    @Column(name = "fecha_compartido", nullable = false)
    private LocalDateTime fechaCompartido = LocalDateTime.now();
}

