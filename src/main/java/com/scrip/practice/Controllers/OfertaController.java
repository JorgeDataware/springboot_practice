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

/**
 * Spring MVC controller for educational offering (Oferta Educativa) operations
 * and the application home page.
 * 
 * <p>Handles both the public student-facing view (card layout) and the admin
 * management console (table + modal). Also serves the standalone form pages
 * for backward compatibility. All form submissions use the POST-Redirect-GET
 * (PRG) pattern with flash attributes for success/error messaging.</p>
 * 
 * <h3>Routes:</h3>
 * <ul>
 *   <li>{@code GET /} - Home page (Inicio)</li>
 *   <li>{@code GET /ofertas} - Public student view with offering cards</li>
 *   <li>{@code GET /consola/gestion_ofertas} - Admin management table + modal</li>
 *   <li>{@code GET /ofertas/nueva} - Standalone form for creating a new offering</li>
 *   <li>{@code GET /ofertas/nueva/{id}} - Standalone form for editing an existing offering</li>
 *   <li>{@code POST /ofertas/guardar} - Create or update an offering</li>
 *   <li>{@code POST /ofertas/eliminar/{id}} - Delete an offering</li>
 * </ul>
 * 
 * @see OfertaEducativa
 * @see OfertaEducativaService
 * @see DivisionService
 */
@Controller
public class OfertaController {
    
    @Autowired
    private OfertaEducativaService ofertaEducativaService;
    
    @Autowired
    private DivisionService divisionService;

    /**
     * Displays the application home page.
     * 
     * @param model Spring MVC model (unused — the template is static)
     * @return the "Inicio" Thymeleaf template
     */
    @GetMapping("/")
    public String inicio(Model model) {
        return "Inicio";
    }

    /**
     * Displays the public student-facing view of educational offerings as cards.
     * 
     * @param model Spring MVC model — receives {@code ofertas} (List of OfertaEducativa)
     * @return the "OfertaEducativa" Thymeleaf template (card layout)
     */
    @GetMapping("/ofertas")
    public String verOfertas(Model model) {
        List<OfertaEducativa> ofertas = ofertaEducativaService.obtenerTodas();
        model.addAttribute("ofertas", ofertas);
        return "OfertaEducativa";
    }

    /**
     * Displays the admin management page with a table of all offerings and an
     * inline Bootstrap modal for creating/editing.
     * 
     * <p>Passes both the offerings list and the divisions list to the template.
     * The divisions list populates the division dropdown inside the modal form.</p>
     * 
     * @param model Spring MVC model — receives {@code ofertas} and {@code divisiones}
     * @return the "GestionOfertas" Thymeleaf template
     */
    @GetMapping("/consola/gestion_ofertas")
    public String gestionOfertas(Model model) {
        List<OfertaEducativa> ofertas = ofertaEducativaService.obtenerTodas();
        List<Division> divisiones = divisionService.obtenerTodas();
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("divisiones", divisiones);
        return "GestionOfertas";
    }

    /**
     * Displays the standalone form page for creating a new offering.
     * 
     * <p>This is an alternative to the modal in GestionOfertas. The template
     * {@code NuevaOferta.html} is a full-page form.</p>
     * 
     * @param model Spring MVC model — receives {@code divisiones} for the dropdown
     * @return the "NuevaOferta" Thymeleaf template
     */
    @GetMapping("/ofertas/nueva")
    public String mostrarFormularioNuevaOferta(Model model) {
        List<Division> divisiones = divisionService.obtenerTodas();
        model.addAttribute("divisiones", divisiones);
        return "NuevaOferta";
    }

    /**
     * Displays the standalone form page for editing an existing offering.
     * 
     * <p>Pre-populates the form with the offering's current data. Throws
     * RuntimeException if the offering is not found.</p>
     * 
     * @param id    the UUID of the offering to edit
     * @param model Spring MVC model — receives {@code oferta} and {@code divisiones}
     * @return the "NuevaOferta" Thymeleaf template (in edit mode)
     * @throws RuntimeException if no offering exists with the given UUID
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
     * Creates or updates an educational offering based on form data.
     * 
     * <p>If {@code id} is present, performs an update; otherwise creates a new
     * offering. Optionally associates the offering with a division if
     * {@code divisionId} is provided. Uses PRG pattern — always redirects
     * back to the management page with a flash message.</p>
     * 
     * @param id                 the offering UUID (null for new offerings)
     * @param nombre             the offering name (required)
     * @param modalidad          the study modality (required)
     * @param imageUrl           URL to the representative image (optional)
     * @param divisionId         the division ID to associate (optional, nullable)
     * @param redirectAttributes used to pass flash messages across the redirect
     * @return redirect to {@code /consola/gestion_ofertas}
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
                
                // Assign division if one was selected
                if (divisionId != null) {
                    Optional<Division> divisionOpt = divisionService.obtenerPorId(divisionId);
                    divisionOpt.ifPresent(ofertaActualizada::setDivision);
                }
                
                ofertaEducativaService.actualizar(id, ofertaActualizada);
                
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La oferta educativa '" + nombre + "' ha sido actualizada exitosamente.");
            } else {
                OfertaEducativa nuevaOferta = new OfertaEducativa(nombre, modalidad, imageUrl);
                
                // Assign division if one was selected
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
            return "redirect:/consola/gestion_ofertas";
        }
    }

    /**
     * Deletes an educational offering by its UUID.
     * 
     * @param id                 the UUID of the offering to delete
     * @param redirectAttributes used to pass flash messages across the redirect
     * @return redirect to {@code /consola/gestion_ofertas}
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
