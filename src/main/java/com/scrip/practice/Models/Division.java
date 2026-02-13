package com.scrip.practice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "division")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @NotEmpty
    private String cve;

    @NotEmpty
    private String name;

    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "division")
    private List<OfertaEducativa> OfertasEducativas;
}
