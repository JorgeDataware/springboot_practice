package com.scrip.practice.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oferta_educativa")
public class OfertaEducativa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;
    
    @Column(name = "modalidad", length = 100)
    private String modalidad;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", nullable = true)
    private Division division;
    
    @Transient
    private PerfilEgreso perfil;

    public OfertaEducativa(String nombre, String modalidad, String imageUrl){
        this.nombre = nombre;
        this.modalidad = modalidad;
        this.imageUrl = imageUrl;
    }
}