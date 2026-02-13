package com.scrip.practice.Controllers;
import com.scrip.practice.Models.Division;
import com.scrip.practice.Models.OfertaEducativa;
import com.scrip.practice.Services.DivisionService;
import com.scrip.practice.Services.OfertaEducativaService;
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
import java.util.UUID;

@Controller
public class OfertaController {
    
    @Autowired
    private OfertaEducativaService ofertaEducativaService;
    
    @Autowired
    private DivisionService divisionService;

    /**
     * GET /
     * Pagina de inicio
     */
    @GetMapping("/")
    public String inicio(Model model) {
        return "Inicio";
    }

    /**
     * GET /ofertas
     * Vista publica de ofertas educativas (vista de estudiante con tarjetas)
     */
    @GetMapping("/ofertas")
    public String verOfertas(Model model) {
        List<OfertaEducativa> ofertas = ofertaEducativaService.obtenerTodas();
        model.addAttribute("ofertas", ofertas);
        return "OfertaEducativa";
    }

    /**
     * GET /consola/gestion_ofertas
     * Pantalla de gestion de ofertas con datatable
     */
    @GetMapping("/consola/gestion_ofertas")
    public String gestionOfertas(Model model) {
        List<OfertaEducativa> ofertas = ofertaEducativaService.obtenerTodas();
        model.addAttribute("ofertas", ofertas);
        return "GestionOfertas";
    }

    /**
     * GET /ofertas/nueva
     * Muestra el formulario para crear una nueva oferta educativa (sin ID)
     */
    @GetMapping("/ofertas/nueva")
    public String mostrarFormularioNuevaOferta(Model model) {
        List<Division> divisiones = divisionService.obtenerTodas();
        model.addAttribute("divisiones", divisiones);
        return "NuevaOferta";
    }

    /**
     * GET /ofertas/nueva/{id}
     * Muestra el formulario para editar una oferta educativa existente
     */
    @GetMapping("/ofertas/nueva/{id}")
    public String mostrarFormularioEditarOferta(@PathVariable UUID id, Model model) {
        Optional<OfertaEducativa> ofertaOpt = ofertaEducativaService.obtenerPorId(id);
        
        if (ofertaOpt.isPresent()) {
            model.addAttribute("oferta", ofertaOpt.get());
            List<Division> divisiones = divisionService.obtenerTodas();
            model.addAttribute("divisiones", divisiones);
            return "NuevaOferta";
        } else {
            throw new RuntimeException("No se encontro la oferta educativa con ID: " + id);
        }
    }

    /**
     * POST /ofertas/guardar
     * Procesa el formulario y guarda o actualiza la oferta educativa
     */
    @PostMapping("/ofertas/guardar")
    public String guardarOferta(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam("nombre") String nombre,
            @RequestParam("modalidad") String modalidad,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "divisionId", required = false) Integer divisionId,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (id != null) {
                OfertaEducativa ofertaActualizada = new OfertaEducativa(nombre, modalidad, imageUrl);
                
                // Asignar division si se selecciono una
                if (divisionId != null) {
                    Optional<Division> divisionOpt = divisionService.obtenerPorId(divisionId);
                    divisionOpt.ifPresent(ofertaActualizada::setDivision);
                }
                
                ofertaEducativaService.actualizar(id, ofertaActualizada);
                
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La oferta educativa '" + nombre + "' ha sido actualizada exitosamente.");
            } else {
                OfertaEducativa nuevaOferta = new OfertaEducativa(nombre, modalidad, imageUrl);
                
                // Asignar division si se selecciono una
                if (divisionId != null) {
                    Optional<Division> divisionOpt = divisionService.obtenerPorId(divisionId);
                    divisionOpt.ifPresent(nuevaOferta::setDivision);
                }
                
                ofertaEducativaService.crear(nuevaOferta);
                
                redirectAttributes.addFlashAttribute("mensaje",
                    "La oferta educativa '" + nombre + "' ha sido agregada exitosamente.");
            }
            
            return "redirect:/consola/gestion_ofertas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ocurrio un error al guardar la oferta educativa: " + e.getMessage());
            
            if (id != null) {
                return "redirect:/ofertas/nueva/" + id;
            } else {
                return "redirect:/ofertas/nueva";
            }
        }
    }

    /**
     * POST /ofertas/eliminar/{id}
     * Elimina una oferta educativa
     */
    @PostMapping("/ofertas/eliminar/{id}")
    public String eliminarOferta(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            ofertaEducativaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "La oferta educativa ha sido eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ocurrio un error al eliminar la oferta educativa: " + e.getMessage());
        }
        return "redirect:/consola/gestion_ofertas";
    }
}
