package com.scrip.practice.Services;

import com.scrip.practice.Models.OfertaEducativa;
import com.scrip.practice.Repositories.OfertaEducativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for {@link OfertaEducativa} CRUD operations.
 * 
 * <p>Class-level {@code @Transactional} ensures all public methods run within
 * a database transaction. The update method uses find-then-set-then-save to
 * ensure only allowed fields are modified (nombre, modalidad, imageUrl, division).
 * Throws {@link RuntimeException} when an offering is not found during update/delete.</p>
 * 
 * @see OfertaEducativa
 * @see OfertaEducativaRepository
 * @see com.scrip.practice.Controllers.OfertaController
 */
@Service
@Transactional
public class OfertaEducativaService {

    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    /**
     * Persists a new educational offering to the database.
     * 
     * @param ofertaEducativa the entity to create
     * @return the saved entity (with generated UUID)
     */
    public OfertaEducativa crear(OfertaEducativa ofertaEducativa) {
        return ofertaEducativaRepository.save(ofertaEducativa);
    }

    /**
     * Retrieves all educational offerings from the database.
     * 
     * @return list of all offerings
     */
    public List<OfertaEducativa> obtenerTodas() {
        return ofertaEducativaRepository.findAll();
    }

    /**
     * Finds a single educational offering by its UUID.
     * 
     * @param id the offering's UUID primary key
     * @return an Optional containing the offering if found
     */
    public Optional<OfertaEducativa> obtenerPorId(UUID id) {
        return ofertaEducativaRepository.findById(id);
    }

    /**
     * Updates an existing offering's nombre, modalidad, imageUrl, and division.
     * 
     * <p>Finds the existing entity by UUID, applies field changes, then saves.
     * The division relationship is also updated (can be set to null to unassign).</p>
     * 
     * @param id                the UUID of the offering to update
     * @param ofertaActualizada entity containing the new field values
     * @return the updated offering
     * @throws RuntimeException if no offering exists with the given UUID
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
     * Deletes an educational offering by UUID. Checks existence first.
     * 
     * @param id the UUID of the offering to delete
     * @throws RuntimeException if no offering exists with the given UUID
     */
    public void eliminar(UUID id) {
        if (!ofertaEducativaRepository.existsById(id)) {
            throw new RuntimeException("Oferta Educativa no encontrada con id: " + id);
        }
        ofertaEducativaRepository.deleteById(id);
    }

    /**
     * Returns the total count of educational offerings in the database.
     * 
     * @return total number of offerings
     */
    public long contarTodas() {
        return ofertaEducativaRepository.count();
    }

    /**
     * Checks whether an offering with the given UUID exists.
     * 
     * @param id the UUID to check
     * @return true if an offering with that UUID exists
     */
    public boolean existe(UUID id) {
        return ofertaEducativaRepository.existsById(id);
    }
}
