package com.scrip.practice.Repositories;

import com.scrip.practice.Models.OfertaEducativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link OfertaEducativa} entities.
 * 
 * <p>Extends {@link JpaRepository} providing standard CRUD operations with
 * {@code UUID} as the primary key type. Additionally defines derived query
 * methods, custom JPQL queries, and a native PostgreSQL query.</p>
 * 
 * @see OfertaEducativa
 * @see com.scrip.practice.Services.OfertaEducativaService
 */
@Repository
public interface OfertaEducativaRepository extends JpaRepository<OfertaEducativa, UUID> {
    
    // ========== Derived query methods (Spring Data JPA auto-generates the SQL) ==========
    
    /**
     * Finds all educational offerings with the given modality.
     * 
     * @param modalidad the exact modality string to match
     * @return list of matching offerings
     */
    List<OfertaEducativa> findByModalidad(String modalidad);
    
    /**
     * Finds a single educational offering by its exact name.
     * 
     * @param nombre the exact name to search for
     * @return an Optional containing the offering if found
     */
    Optional<OfertaEducativa> findByNombre(String nombre);
    
    /**
     * Finds all offerings whose name contains the given text (case-insensitive).
     * 
     * @param nombre the partial name to search for
     * @return list of matching offerings
     */
    List<OfertaEducativa> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Finds all offerings with the given modality, sorted by name ascending.
     * 
     * @param modalidad the exact modality to filter by
     * @return sorted list of matching offerings
     */
    List<OfertaEducativa> findByModalidadOrderByNombreAsc(String modalidad);
    
    /**
     * Checks whether an offering with the given name already exists.
     * 
     * @param nombre the name to check
     * @return true if an offering with that name exists
     */
    boolean existsByNombre(String nombre);
    
    
    // ========== Custom JPQL queries ==========
    
    /**
     * Full-text search across nombre and modalidad fields (case-insensitive LIKE).
     * 
     * @param texto the search text to match against nombre or modalidad
     * @return list of offerings matching the search text in either field
     */
    @Query("SELECT o FROM OfertaEducativa o WHERE LOWER(o.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(o.modalidad) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<OfertaEducativa> buscarPorTexto(@Param("texto") String texto);
    
    /**
     * Counts the number of offerings with the given modality.
     * 
     * @param modalidad the modality to count
     * @return the count of offerings with that modality
     */
    @Query("SELECT COUNT(o) FROM OfertaEducativa o WHERE o.modalidad = :modalidad")
    long contarPorModalidad(@Param("modalidad") String modalidad);
    
    // ========== Native SQL queries (PostgreSQL-specific) ==========
    
    /**
     * Retrieves offerings by modality with a row limit. Uses native PostgreSQL
     * {@code LIMIT} clause.
     * 
     * @param modalidad the modality to filter by
     * @param limite    maximum number of rows to return
     * @return list of offerings (up to the specified limit)
     */
    @Query(value = "SELECT * FROM oferta_educativa WHERE modalidad = :modalidad LIMIT :limite", nativeQuery = true)
    List<OfertaEducativa> obtenerPorModalidadConLimite(@Param("modalidad") String modalidad, @Param("limite") int limite);
}
