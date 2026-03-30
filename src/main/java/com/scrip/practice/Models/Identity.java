package com.scrip.practice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
@Table(name = "institution_identity") // 'Identity' es palabra reservada en muchos SQL
public class Identity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Cambiado a Integer para permitir null

    @NotEmpty
    private String name;

    @NotEmpty
    private String mision; // minúscula

    @NotEmpty
    private String vision; // minúscula

    @NotEmpty
    private String policy; // minúscula

    @NotEmpty
    private String sgeObjectives; // minúscula (camelCase)

    @NotEmpty
    private String values; // minúscula

    private boolean active;
}