package com.recetas.recetas.service;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnuncioService {
    
    @Autowired
    private AnuncioRepository anuncioRepository;

    public List<Anuncio> obtenerAnunciosActivos() {
        return anuncioRepository.findByActivoTrue();
    }
}
