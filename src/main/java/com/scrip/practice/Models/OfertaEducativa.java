package com.scrip.practice.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

/**
 * JPA entity representing an educational offering (career/program) such as
 * "Ingenieria en Sistemas Computacionales".
 * 
 * <p>Mapped to the {@code oferta_educativa} table in PostgreSQL. Uses UUID as
 * the primary key (auto-generated via {@link GenerationType#UUID}).</p>
 * 
 * <p>Each offering optionally belongs to a {@link Division} through a many-to-one
 * lazy-loaded relationship. The {@code perfil} field is transient (not persisted)
 * and holds graduation profile data fetched at runtime.</p>
 * 
 * @see Division
 * @see PerfilEgreso
 * @see com.scrip.practice.Repositories.OfertaEducativaRepository
 * @see com.scrip.practice.Services.OfertaEducativaService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oferta_educativa")
public class OfertaEducativa {
    
    /** Auto-generated UUID primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    /** Name of the educational offering (e.g. "Ingenieria Mecatronica"). */
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;
    
    /** Study modality (e.g. "Modalidad intensiva y mixta", "Modalidad presencial"). */
    @Column(name = "modalidad", length = 100)
    private String modalidad;
    
    /** URL to the representative image for this offering. */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    /** 
     * The division this offering belongs to. Nullable — an offering may not
     * be assigned to any division.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", nullable = true)
    private Division division;
    
    /** 
     * Transient graduation profile data (not persisted to DB).
     * Populated at runtime when needed.
     */
    @Transient
    private PerfilEgreso perfil;

    /**
     * Convenience constructor for creating a new offering without an ID or division.
     * 
     * @param nombre   the name of the educational offering
     * @param modalidad the study modality
     * @param imageUrl  URL to the representative image (may be null)
     */
    public OfertaEducativa(String nombre, String modalidad, String imageUrl){
        this.nombre = nombre;
        this.modalidad = modalidad;
        this.imageUrl = imageUrl;
    }
}