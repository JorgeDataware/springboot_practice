package com.scrip.practice.Models;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Plain POJO representing a graduation profile for an educational offering.
 * 
 * <p>This is NOT a JPA entity — it is not persisted to the database. It is used
 * as a {@code @Transient} field in {@link OfertaEducativa} and populated at
 * runtime (e.g. from an external API or computed data).</p>
 * 
 * <p>Contains a description of the graduate profile along with two skill lists:
 * transversal (soft/general) skills and specific (technical/domain) skills.</p>
 * 
 * @see OfertaEducativa#perfil
 */
@Data
public class PerfilEgreso {

    /** Unique identifier for this graduation profile. */
    public UUID Id;

    /** Textual description of the graduation profile. */
    public String Description;

    /** List of transversal (soft/general) skills. */
    public List<String> HabilidadesTransversales;

    /** List of specific (technical/domain) skills. */
    public List<String> HabilidadesEspecificas;

    /**
     * Full constructor.
     * 
     * @param _Id                         unique identifier
     * @param _description                profile description
     * @param _habilidadesTransversales   transversal skills list
     * @param _habilidadesEspecificas     specific skills list
     */
    public PerfilEgreso(UUID _Id, String _description, List<String> _habilidadesTransversales, List<String> _habilidadesEspecificas){
        this.Id = _Id;
        this.Description = _description;
        this.HabilidadesTransversales = _habilidadesTransversales;
        this.HabilidadesEspecificas = _habilidadesEspecificas;
    }

    /** No-arg constructor for deserialization/framework use. */
    public PerfilEgreso(){

    }
}
