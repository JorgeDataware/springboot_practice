package com.scrip.practice.Repositories;

import com.scrip.practice.Models.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Integer> {

    // Para obtener la identidad que se mostrará al público
    Optional<Identity> findByActiveTrue();

    // Query para desactivar todos (útil para asegurar que solo haya uno activo)
    @Modifying
    @Transactional
    @Query("UPDATE Identity i SET i.active = false")
    void deactivateAll();
}