package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long>{
    List<Receta> findTop6ByOrderByFechaCreacionDesc();
    List<Receta> findTop6ByOrderByPopularidadDesc();
    List<Receta> findByNombreContainingIgnoreCase(String nombre);
    List<Receta> findByTipoCocinaContainingIgnoreCase(String tipoCocina);
    List<Receta> findByPaisOrigenContainingIgnoreCase(String paisOrigen);
    List<Receta> findByDificultadContainingIgnoreCase(String dificultad);
    List<Receta> findByIngredientesContainingIgnoreCase(String ingrediente);
}
