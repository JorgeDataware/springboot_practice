package com.scrip.practice.Services;

import com.scrip.practice.Models.Division;
import com.scrip.practice.Repositories.DivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for {@link Division} CRUD operations.
 * 
 * <p>Delegates persistence to {@link DivisionRepository}. The update method uses
 * a find-then-set-then-save pattern to ensure only the allowed fields are modified.
 * Throws {@link RuntimeException} when a division is not found during update/delete.</p>
 * 
 * @see Division
 * @see DivisionRepository
 * @see com.scrip.practice.Controllers.DivisionController
 */
@Service
public class DivisionService {

    @Autowired
    private DivisionRepository divisionRepository;

    /**
     * Retrieves all divisions from the database.
     * 
     * @return list of all divisions
     */
    public List<Division> obtenerTodas() {
        return divisionRepository.findAll();
    }

    /**
     * Finds a single division by its integer ID.
     * 
     * @param id the division's primary key
     * @return an Optional containing the division if found
     */
    public Optional<Division> obtenerPorId(Integer id) {
        return divisionRepository.findById(id);
    }

    /**
     * Persists a new division to the database.
     * 
     * @param division the division entity to create
     * @return the saved division (with generated ID)
     */
    public Division crear(Division division) {
        return divisionRepository.save(division);
    }

    /**
     * Updates an existing division's cve, name, and active fields.
     * 
     * <p>Finds the existing entity by ID, applies field changes, then saves.
     * This pattern ensures the entity's relationships (e.g. OfertasEducativas)
     * are preserved during update.</p>
     * 
     * @param id                  the ID of the division to update
     * @param divisionActualizada entity containing the new field values
     * @return the updated division
     * @throws RuntimeException if no division exists with the given ID
     */
    public Division actualizar(Integer id, Division divisionActualizada) {
        return divisionRepository.findById(id)
                .map(division -> {
                    division.setCve(divisionActualizada.getCve());
                    division.setName(divisionActualizada.getName());
                    division.setActive(divisionActualizada.isActive());
                    return divisionRepository.save(division);
                })
                .orElseThrow(() -> new RuntimeException("Division no encontrada con ID: " + id));
    }

    /**
     * Deletes a division by ID. Checks existence first and throws if not found.
     * 
     * <p><strong>Warning:</strong> Due to {@code CascadeType.ALL} + orphan removal on
     * the Division entity, deleting a division will also delete all its associated
     * educational offerings.</p>
     * 
     * @param id the ID of the division to delete
     * @throws RuntimeException if no division exists with the given ID
     */
    public void eliminar(Integer id) {
        if (!divisionRepository.existsById(id)) {
            throw new RuntimeException("Division no encontrada con ID: " + id);
        }
        divisionRepository.deleteById(id);
    }

    /**
     * Returns the total count of divisions in the database.
     * 
     * @return total number of divisions
     */
    public long contarTodas() {
        return divisionRepository.count();
    }

    /**
     * Checks whether a division with the given ID exists.
     * 
     * @param id the ID to check
     * @return true if a division with that ID exists
     */
    public boolean existe(Integer id) {
        return divisionRepository.existsById(id);
    }
}
