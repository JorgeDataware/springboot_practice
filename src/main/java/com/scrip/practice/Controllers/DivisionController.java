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

/**
 * Spring MVC controller for Division CRUD operations.
 * 
 * <p>Handles the admin management page for divisions. All form submissions
 * use the POST-Redirect-GET (PRG) pattern with flash attributes for
 * success/error messaging. The create and update operations share the
 * same endpoint ({@code POST /divisiones/guardar}), distinguished by
 * the presence of the {@code id} parameter.</p>
 * 
 * <h3>Routes:</h3>
 * <ul>
 *   <li>{@code GET /consola/gestion_divisiones} - Management page with table + modal</li>
 *   <li>{@code POST /divisiones/guardar} - Create or update a division</li>
 *   <li>{@code POST /divisiones/eliminar/{id}} - Delete a division</li>
 * </ul>
 * 
 * @see Division
 * @see DivisionService
 */
@Controller
public class DivisionController {
    
    @Autowired
    private DivisionService divisionService;

    /**
     * Displays the division management page with a table of all divisions.
     * 
     * <p>The page includes an inline Bootstrap modal for creating/editing
     * divisions (see {@code GestionDivisiones.html}).</p>
     * 
     * @param model Spring MVC model — receives {@code divisiones} (List of Division)
     * @return the "GestionDivisiones" Thymeleaf template
     */
    @GetMapping("/consola/gestion_divisiones")
    public String gestionDivisiones(Model model) {
        List<Division> divisiones = divisionService.obtenerTodas();
        model.addAttribute("divisiones", divisiones);
        return "GestionDivisiones";
    }

    /**
     * Creates or updates a division based on form data from the modal.
     * 
     * <p>If {@code id} is present, performs an update; otherwise creates a new
     * division. Uses PRG pattern — always redirects back to the management page
     * with a flash attribute containing the result message.</p>
     * 
     * @param id                 the division ID (null for new divisions)
     * @param cve                the division code/key
     * @param name               the division name
     * @param active             whether the division is active (defaults to false)
     * @param redirectAttributes used to pass flash messages across the redirect
     * @return redirect to {@code /consola/gestion_divisiones}
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
            return "redirect:/consola/gestion_divisiones";
        }
    }

    /**
     * Deletes a division by its ID.
     * 
     * <p><strong>Warning:</strong> Due to cascade settings on the Division entity,
     * deleting a division will also delete all its associated educational offerings.</p>
     * 
     * @param id                 the ID of the division to delete
     * @param redirectAttributes used to pass flash messages across the redirect
     * @return redirect to {@code /consola/gestion_divisiones}
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
