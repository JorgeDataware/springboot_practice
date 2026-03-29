package com.scrip.practice.Controllers;

import com.scrip.practice.Services.EmailService;
import com.scrip.practice.Services.PasswordResetService;
import com.scrip.practice.dto.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/send-email/form")
    public String showEmailForm(Model model, EmailDto dto) {
        model.addAttribute("emailDTO", dto);
        return "/emailForm";
    }
    
    @PostMapping("/send-email")
    public String sendEmail(@ModelAttribute EmailDto dto) {
        try {
            emailService.SendMail(dto);
        } catch (Exception e) {
            logger.error("Error al enviar el email", e);
        }
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        // Llama al método del servicio que armamos en el paso anterior
        passwordResetService.createPasswordResetTokenForUser(email);

        // Mensaje flash que se mostrará en la página a la que redirigimos
        redirectAttributes.addFlashAttribute("mensaje", "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        if (!passwordResetService.isValidToken(token)) {
            redirectAttributes.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/login";
        }
        // Pasamos el token a la vista para meterlo en un input oculto
        model.addAttribute("token", token);
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       RedirectAttributes redirectAttributes) {
        if (passwordResetService.isValidToken(token)) {
            passwordResetService.changeUserPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("exito", "¡Tu contraseña ha sido restablecida exitosamente! Ya puedes iniciar sesión.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
