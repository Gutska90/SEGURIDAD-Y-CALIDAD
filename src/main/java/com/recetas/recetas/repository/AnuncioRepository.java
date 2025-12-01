package com.recetas.recetas.repository;

import com.recetas.recetas.model.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long>{
    
    @Query("SELECT a FROM Anuncio a WHERE a.activo = 1")
    List<Anuncio> findByActivoTrue();
}
