package com.scrip.practice.Repositories;

import com.scrip.practice.Models.OfertaEducativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfertaEducativaRepository extends JpaRepository<OfertaEducativa, UUID> {
    
    // ========== Métodos de Query derivados (Spring Data JPA genera la query automáticamente) ==========
    
    /**
     * Busca ofertas educativas por modalidad
     * Spring Data JPA crea automáticamente: SELECT * FROM oferta_educativa WHERE modalidad = ?
     */
    List<OfertaEducativa> findByModalidad(String modalidad);
    
    /**
     * Busca una oferta educativa por nombre exacto
     */
    Optional<OfertaEducativa> findByNombre(String nombre);
    
    /**
     * Busca ofertas educativas cuyo nombre contenga el texto (case insensitive)
     */
    List<OfertaEducativa> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca ofertas educativas por modalidad ordenadas por nombre
     */
    List<OfertaEducativa> findByModalidadOrderByNombreAsc(String modalidad);
    
    /**
     * Verifica si existe una oferta educativa con ese nombre
     */
    boolean existsByNombre(String nombre);
    
    
    // ========== Queries personalizadas con @Query ==========
    
    /**
     * Busca ofertas educativas usando JPQL (Java Persistence Query Language)
     */
    @Query("SELECT o FROM OfertaEducativa o WHERE LOWER(o.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(o.modalidad) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<OfertaEducativa> buscarPorTexto(@Param("texto") String texto);
    
    /**
     * Cuenta ofertas educativas por modalidad usando JPQL
     */
    @Query("SELECT COUNT(o) FROM OfertaEducativa o WHERE o.modalidad = :modalidad")
    long contarPorModalidad(@Param("modalidad") String modalidad);
    
    /**
     * Query nativa SQL (útil para queries complejas específicas de PostgreSQL)
     */
    @Query(value = "SELECT * FROM oferta_educativa WHERE modalidad = :modalidad LIMIT :limite", nativeQuery = true)
    List<OfertaEducativa> obtenerPorModalidadConLimite(@Param("modalidad") String modalidad, @Param("limite") int limite);
}
