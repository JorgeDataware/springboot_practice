package com.scrip.practice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * JPA entity representing an organizational division (e.g. "Division de Tecnologias").
 * 
 * <p>Mapped to the {@code division} table in PostgreSQL. Uses auto-increment integer
 * primary key ({@link GenerationType#IDENTITY}). Each division can have multiple
 * {@link OfertaEducativa} entries associated to it via a one-to-many relationship.</p>
 * 
 * <p>Cascade is set to ALL with orphan removal enabled, meaning deleting a division
 * will also delete all its associated educational offerings.</p>
 * 
 * @see OfertaEducativa
 * @see com.scrip.practice.Repositories.DivisionRepository
 * @see com.scrip.practice.Services.DivisionService
 */
@Data
@Entity
@Table(name = "division")
public class Division {

    /** Auto-generated integer primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    /** Unique code/key identifying the division (e.g. "DTAI"). */
    @NotEmpty
    private String cve;

    /** Human-readable name of the division. */
    @NotEmpty
    private String name;

    /** Whether this division is currently active. */
    private boolean active;

    /** 
     * List of educational offerings belonging to this division.
     * Cascade ALL + orphan removal: deleting the division removes its offerings.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "division")
    private List<OfertaEducativa> OfertasEducativas;
}
