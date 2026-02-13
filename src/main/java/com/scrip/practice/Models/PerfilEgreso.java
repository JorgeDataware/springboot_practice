package com.scrip.practice.Models;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PerfilEgreso {
    public UUID Id;
    public String Description;
    public List<String> HabilidadesTransversales;
    public List<String> HabilidadesEspecificas;

    // Constructor
    public PerfilEgreso(UUID _Id, String _description, List<String> _habilidadesTransversales, List<String> _habilidadesEspecificas){
        this.Id = _Id;
        this.Description = _description;
        this.HabilidadesTransversales = _habilidadesTransversales;
        this.HabilidadesEspecificas = _habilidadesEspecificas;
    }

    public PerfilEgreso(){

    }
}
