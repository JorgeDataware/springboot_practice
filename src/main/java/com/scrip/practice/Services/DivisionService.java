package com.scrip.practice.Services;

import com.scrip.practice.Models.Division;
import com.scrip.practice.Repositories.DivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DivisionService {

    @Autowired
    private DivisionRepository divisionRepository;

    /**
     * Obtiene todas las divisiones
     */
    public List<Division> obtenerTodas() {
        return divisionRepository.findAll();
    }

    /**
     * Obtiene una division por ID
     */
    public Optional<Division> obtenerPorId(Integer id) {
        return divisionRepository.findById(id);
    }

    /**
     * Crea una nueva division
     */
    public Division crear(Division division) {
        return divisionRepository.save(division);
    }

    /**
     * Actualiza una division existente
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
     * Elimina una division
     */
    public void eliminar(Integer id) {
        if (!divisionRepository.existsById(id)) {
            throw new RuntimeException("Division no encontrada con ID: " + id);
        }
        divisionRepository.deleteById(id);
    }

    /**
     * Cuenta todas las divisiones
     */
    public long contarTodas() {
        return divisionRepository.count();
    }

    /**
     * Verifica si existe una division
     */
    public boolean existe(Integer id) {
        return divisionRepository.existsById(id);
    }
}
