package com.scrip.practice.Repositories;

import com.scrip.practice.Models.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Division} entities.
 * 
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository}
 * (findAll, findById, save, deleteById, existsById, count, etc.).
 * Uses {@code Integer} as the primary key type.</p>
 * 
 * <p>No custom query methods are defined — all operations use the built-in
 * JpaRepository methods.</p>
 * 
 * @see Division
 * @see com.scrip.practice.Services.DivisionService
 */
@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {

}
