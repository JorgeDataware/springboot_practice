package com.scrip.practice.Controllers;

import com.scrip.practice.Models.Division;
import com.scrip.practice.Models.OfertaEducativa;
import com.scrip.practice.Services.DivisionService;
import com.scrip.practice.Services.OfertaEducativaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API controller for OfertaEducativa CRUD operations.
 *
 * <p>Returns JSON responses instead of redirects, enabling the frontend
 * to perform create/update/delete operations via fetch() without page reloads.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code GET    /api/ofertas}       - List all offerings</li>
 *   <li>{@code POST   /api/ofertas}       - Create a new offering</li>
 *   <li>{@code PUT    /api/ofertas/{id}}   - Update an existing offering</li>
 *   <li>{@code DELETE /api/ofertas/{id}}   - Delete an offering</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/ofertas")
public class OfertaApiController {

    @Autowired
    private OfertaEducativaService ofertaEducativaService;

    @Autowired
    private DivisionService divisionService;

    /**
     * Returns all offerings as a JSON array.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<OfertaEducativa> ofertas = ofertaEducativaService.obtenerTodas();
        List<Map<String, Object>> result = new ArrayList<>();
        for (OfertaEducativa o : ofertas) {
            result.add(toMap(o));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new offering from JSON body.
     *
     * @param datos map with keys: nombre, modalidad, imageUrl, divisionId
     * @return the created offering
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Map<String, Object> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            OfertaEducativa nueva = new OfertaEducativa(
                    (String) datos.get("nombre"),
                    (String) datos.get("modalidad"),
                    (String) datos.get("imageUrl")
            );

            // Assign division if provided
            Object divIdObj = datos.get("divisionId");
            if (divIdObj != null && !divIdObj.toString().isEmpty()) {
                Integer divisionId = Integer.parseInt(divIdObj.toString());
                Optional<Division> divOpt = divisionService.obtenerPorId(divisionId);
                divOpt.ifPresent(nueva::setDivision);
            }

            OfertaEducativa guardada = ofertaEducativaService.crear(nueva);

            response.put("success", true);
            response.put("mensaje", "La oferta educativa '" + guardada.getNombre() + "' ha sido agregada exitosamente.");
            response.put("oferta", toMap(guardada));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al guardar la oferta educativa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Updates an existing offering.
     *
     * @param id    the offering UUID
     * @param datos map with keys: nombre, modalidad, imageUrl, divisionId
     * @return the updated offering
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable UUID id, @RequestBody Map<String, Object> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            OfertaEducativa actualizada = new OfertaEducativa(
                    (String) datos.get("nombre"),
                    (String) datos.get("modalidad"),
                    (String) datos.get("imageUrl")
            );

            // Assign division if provided
            Object divIdObj = datos.get("divisionId");
            if (divIdObj != null && !divIdObj.toString().isEmpty()) {
                Integer divisionId = Integer.parseInt(divIdObj.toString());
                Optional<Division> divOpt = divisionService.obtenerPorId(divisionId);
                divOpt.ifPresent(actualizada::setDivision);
            }

            OfertaEducativa guardada = ofertaEducativaService.actualizar(id, actualizada);

            response.put("success", true);
            response.put("mensaje", "La oferta educativa '" + guardada.getNombre() + "' ha sido actualizada exitosamente.");
            response.put("oferta", toMap(guardada));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al actualizar la oferta educativa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Deletes an offering by UUID.
     *
     * @param id the offering UUID
     * @return success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ofertaEducativaService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "La oferta educativa ha sido eliminada exitosamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Ocurrio un error al eliminar la oferta educativa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Converts an OfertaEducativa entity to a simple map to avoid serialization
     * issues with lazy-loaded relationships and circular references.
     */
    private Map<String, Object> toMap(OfertaEducativa o) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", o.getId() != null ? o.getId().toString() : null);
        map.put("nombre", o.getNombre());
        map.put("modalidad", o.getModalidad());
        map.put("imageUrl", o.getImageUrl());
        if (o.getDivision() != null) {
            Map<String, Object> divMap = new HashMap<>();
            divMap.put("id", o.getDivision().getId());
            divMap.put("name", o.getDivision().getName());
            map.put("division", divMap);
        } else {
            map.put("division", null);
        }
        return map;
    }
}
