package com.scrip.practice.Services;

import com.scrip.practice.Models.OfertaEducativa;
import com.scrip.practice.Repositories.OfertaEducativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OfertaEducativaService {

    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    /**
     * Guarda una nueva oferta educativa
     */
    public OfertaEducativa crear(OfertaEducativa ofertaEducativa) {
        return ofertaEducativaRepository.save(ofertaEducativa);
    }

    /**
     * Obtiene todas las ofertas educativas
     */
    public List<OfertaEducativa> obtenerTodas() {
        return ofertaEducativaRepository.findAll();
    }

    /**
     * Busca una oferta educativa por ID
     */
    public Optional<OfertaEducativa> obtenerPorId(UUID id) {
        return ofertaEducativaRepository.findById(id);
    }

    /**
     * Actualiza una oferta educativa existente
     */
    public OfertaEducativa actualizar(UUID id, OfertaEducativa ofertaActualizada) {
        return ofertaEducativaRepository.findById(id)
                .map(oferta -> {
                    oferta.setNombre(ofertaActualizada.getNombre());
                    oferta.setModalidad(ofertaActualizada.getModalidad());
                    oferta.setImageUrl(ofertaActualizada.getImageUrl());
                    oferta.setDivision(ofertaActualizada.getDivision());
                    return ofertaEducativaRepository.save(oferta);
                })
                .orElseThrow(() -> new RuntimeException("Oferta Educativa no encontrada con id: " + id));
    }

    /**
     * Elimina una oferta educativa por ID
     */
    public void eliminar(UUID id) {
        if (!ofertaEducativaRepository.existsById(id)) {
            throw new RuntimeException("Oferta Educativa no encontrada con id: " + id);
        }
        ofertaEducativaRepository.deleteById(id);
    }

    /**
     * Cuenta el total de ofertas educativas
     */
    public long contarTodas() {
        return ofertaEducativaRepository.count();
    }

    /**
     * Verifica si existe una oferta educativa por ID
     */
    public boolean existe(UUID id) {
        return ofertaEducativaRepository.existsById(id);
    }
}
