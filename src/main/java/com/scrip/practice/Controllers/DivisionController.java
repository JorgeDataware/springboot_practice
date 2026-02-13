package com.scrip.practice.Controllers;

import com.scrip.practice.Models.Division;
import com.scrip.practice.Services.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class DivisionController {
    
    @Autowired
    private DivisionService divisionService;

    /**
     * GET /consola/gestion_divisiones
     * Pantalla de gestion de divisiones con tabla
     */
    @GetMapping("/consola/gestion_divisiones")
    public String gestionDivisiones(Model model) {
        List<Division> divisiones = divisionService.obtenerTodas();
        model.addAttribute("divisiones", divisiones);
        return "GestionDivisiones";
    }

    /**
     * GET /divisiones/nueva
     * Muestra el formulario para crear una nueva division (sin ID)
     */
    @GetMapping("/divisiones/nueva")
    public String mostrarFormularioNuevaDivision(Model model) {
        return "NuevaDivision";
    }

    /**
     * GET /divisiones/nueva/{id}
     * Muestra el formulario para editar una division existente
     */
    @GetMapping("/divisiones/nueva/{id}")
    public String mostrarFormularioEditarDivision(@PathVariable Integer id, Model model) {
        Optional<Division> divisionOpt = divisionService.obtenerPorId(id);
        
        if (divisionOpt.isPresent()) {
            model.addAttribute("division", divisionOpt.get());
            return "NuevaDivision";
        } else {
            throw new RuntimeException("No se encontro la division con ID: " + id);
        }
    }

    /**
     * POST /divisiones/guardar
     * Procesa el formulario y guarda o actualiza la division
     */
    @PostMapping("/divisiones/guardar")
    public String guardarDivision(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("cve") String cve,
            @RequestParam("name") String name,
            @RequestParam(value = "active", required = false, defaultValue = "false") boolean active,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (id != null) {
                Division divisionActualizada = new Division();
                divisionActualizada.setCve(cve);
                divisionActualizada.setName(name);
                divisionActualizada.setActive(active);
                divisionService.actualizar(id, divisionActualizada);
                
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La division '" + name + "' ha sido actualizada exitosamente.");
            } else {
                Division nuevaDivision = new Division();
                nuevaDivision.setCve(cve);
                nuevaDivision.setName(name);
                nuevaDivision.setActive(active);
                divisionService.crear(nuevaDivision);
                
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La division '" + name + "' ha sido agregada exitosamente.");
            }
            
            return "redirect:/consola/gestion_divisiones";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ocurrio un error al guardar la division: " + e.getMessage());
            
            if (id != null) {
                return "redirect:/divisiones/nueva/" + id;
            } else {
                return "redirect:/divisiones/nueva";
            }
        }
    }

    /**
     * POST /divisiones/eliminar/{id}
     * Elimina una division
     */
    @PostMapping("/divisiones/eliminar/{id}")
    public String eliminarDivision(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            divisionService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "La division ha sido eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ocurrio un error al eliminar la division: " + e.getMessage());
        }
        return "redirect:/consola/gestion_divisiones";
    }
}
