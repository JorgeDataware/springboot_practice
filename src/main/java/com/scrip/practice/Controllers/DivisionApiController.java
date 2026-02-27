package com.scrip.practice.Controllers;

import com.scrip.practice.Models.Division;
import com.scrip.practice.Services.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for Division CRUD operations.
 *
 * <p>Returns JSON responses instead of redirects, enabling the frontend
 * to perform create/update/delete operations via fetch() without page reloads.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code GET    /api/divisiones}       - List all divisions</li>
 *   <li>{@code POST   /api/divisiones}       - Create a new division</li>
 *   <li>{@code PUT    /api/divisiones/{id}}   - Update an existing division</li>
 *   <li>{@code DELETE /api/divisiones/{id}}   - Delete a division</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/divisiones")
public class DivisionApiController {

    @Autowired
    private DivisionService divisionService;

    /**
     * Returns all divisions as a JSON array.
     */
    @GetMapping
    public ResponseEntity<List<Division>> listar() {
        return ResponseEntity.ok(divisionService.obtenerTodas());
    }

    /**
     * Creates a new division from JSON body.
     *
     * @param datos map with keys: cve, name, active
     * @return the created division with generated ID
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Map<String, Object> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            Division nueva = new Division();
            nueva.setCve((String) datos.get("cve"));
            nueva.setName((String) datos.get("name"));
            nueva.setActive(Boolean.TRUE.equals(datos.get("active")));

            Division guardada = divisionService.crear(nueva);

            response.put("success", true);
            response.put("mensaje", "La division '" + guardada.getName() + "' ha sido agregada exitosamente.");
            response.put("division", toMap(guardada));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al guardar la division: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Updates an existing division.
     *
     * @param id    the division ID
     * @param datos map with keys: cve, name, active
     * @return the updated division
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Integer id, @RequestBody Map<String, Object> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            Division actualizada = new Division();
            actualizada.setCve((String) datos.get("cve"));
            actualizada.setName((String) datos.get("name"));
            actualizada.setActive(Boolean.TRUE.equals(datos.get("active")));

            Division guardada = divisionService.actualizar(id, actualizada);

            response.put("success", true);
            response.put("mensaje", "La division '" + guardada.getName() + "' ha sido actualizada exitosamente.");
            response.put("division", toMap(guardada));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al actualizar la division: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Deletes a division by ID.
     *
     * @param id the division ID
     * @return success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            divisionService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "La division ha sido eliminada exitosamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al eliminar la division: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Converts a Division entity to a simple map to avoid serialization issues
     * with the bidirectional OneToMany relationship.
     */
    private Map<String, Object> toMap(Division d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", d.getId());
        map.put("cve", d.getCve());
        map.put("name", d.getName());
        map.put("active", d.isActive());
        return map;
    }
}
