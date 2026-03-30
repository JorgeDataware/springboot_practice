package com.scrip.practice.Controllers;

import com.scrip.practice.Models.Identity;
import com.scrip.practice.Services.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/alumno")
public class AlumnoController {

    @Autowired
    public IdentityService identityService;

    @GetMapping("/admisiones")
    public String admisiones() {
        return "Admisiones";
    }

//    @GetMapping("/valores")
//    public String valores(Model model) {
//        // Lista de valores de la universidad
//        List<String> valores = Arrays.asList(
//                "Excelencia académica",
//                "Innovación y creatividad",
//                "Responsabilidad social",
//                "Integridad y ética",
//                "Respeto y diversidad",
//                "Trabajo en equipo",
//                "Compromiso con la comunidad",
//                "Desarrollo sostenible"
//        );
//        model.addAttribute("valores", valores);
//        return "Valores";
//    }

    @GetMapping("/mapa-sitio")
    public String mapaSitio(Model model) {
        return "MapaSitio";
    }

    @GetMapping("/valores")
    public String showInstitutionalIdentity(Model model) {
        // Solo obtenemos la que está marcada como activa
        Identity activeIdentity = identityService.getActive();
        model.addAttribute("identidad", activeIdentity);
        return "identidad";
    }
}
