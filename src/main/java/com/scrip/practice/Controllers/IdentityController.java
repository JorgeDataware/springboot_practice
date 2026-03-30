package com.scrip.practice.Controllers;

import com.scrip.practice.Models.Identity;
import com.scrip.practice.Services.IdentityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/consola/identidad")
public class IdentityController {

    private static final Logger logger = LoggerFactory.getLogger(IdentityController.class);

    @Autowired
    private IdentityService identityService;

    // Listado de todas las identidades
    @GetMapping
    public String list(Model model) {
        logger.info("GET /consola/identidad - Cargando listado de identidades");
        model.addAttribute("identidades", identityService.getAll());
        // Agregamos un objeto vacío por si el modal lo necesita para el binding inicial
        model.addAttribute("identity", new Identity());
        return "identity/list";
    }

    // Alias para /list
    @GetMapping("/list")
    public String listAlias(Model model) {
        logger.info("GET /consola/identidad/list - Redirigiendo a listado principal");
        return list(model);
    }

    // Ya no necesitamos showCreateForm ni showEditForm porque usamos JS y Modales
    // Pero si los dejas, deben redirigir o mostrar la lista:
    @GetMapping("/nuevo")
    public String createForm() {
        return "redirect:/consola/identidad";
    }

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute("identity") Identity identity,
                       BindingResult result,
                       Model model) {
        logger.info("POST /consola/identidad/guardar - Intentando guardar identity");
        logger.info("Identity recibido: id={}, name={}, active={}", identity.getId(), identity.getName(), identity.isActive());
        
        if (result.hasErrors()) {
            logger.error("Errores de validación encontrados:");
            result.getAllErrors().forEach(error -> logger.error(" - {}", error.getDefaultMessage()));
            // Si hay errores, volvemos a cargar la lista para mostrarla en la misma página
            model.addAttribute("identidades", identityService.getAll());
            // Aquí podrías agregar lógica para reabrir el modal con los errores,
            // pero por ahora, regresemos a la lista para evitar el crash.
            return "identity/list";
        }

        identityService.save(identity);
        logger.info("Identity guardado exitosamente con id={}", identity.getId());
        return "redirect:/consola/identidad";
    }

    // Formulario de edición
    @GetMapping("/editar/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        logger.info("GET /consola/identidad/editar/{} - Cargando formulario de edición", id);
        model.addAttribute("identity", identityService.getById(id));
        return "identity/form";
    }

    // Activar una identidad específica
    @GetMapping("/activar/{id}")
    public String activate(@PathVariable Integer id) {
        logger.info("GET /consola/identidad/activar/{} - Activando identidad", id);
        identityService.activate(id);
        return "redirect:/consola/identidad";
    }

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable Integer id) {
        logger.info("GET /consola/identidad/eliminar/{} - Eliminando identidad", id);
        identityService.delete(id);
        return "redirect:/consola/identidad";
    }
}