package com.scrip.practice.Services;

import com.scrip.practice.dto.EmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    @Autowired
    private InMemoryUserDetailsManager userDetailsManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Diccionario en memoria: Key = Token, Value = Email del usuario
    private Map<String, String> resetTokens = new ConcurrentHashMap<>();

    // 1. Generar token y enviar correo
    public void createPasswordResetTokenForUser(String email) {
        if (userDetailsManager.userExists(email)) {
            String token = UUID.randomUUID().toString();
            resetTokens.put(token, email);

            // Imprimimos en consola para confirmar que entró aquí
            System.out.println("Generando token para: " + email);
            System.out.println("Token generado: " + token);

            try {
                // DESCOMENTAR Y ARMAR EL CORREO:
                EmailDto dto = new EmailDto();
                dto.setTo(email);
                dto.setSubject("Restablece tu contraseña");

                // se arma el enlace para enviar por correo
                String enlace = "http://localhost:8080/reset-password?token=" + token;
                dto.setBody("Para restablecer tu contraseña, haz clic en el siguiente enlace: " + enlace);

                System.out.println("Intentando enviar el correo...");
                emailService.SendMail(dto);
                System.out.println("¡Correo enviado a la cola de salida de Spring!");

            } catch (Exception e) {
                // Si algo falla al enviar, lo veremos en rojo en la consola
                System.err.println("ERROR CRÍTICO AL ENVIAR EL CORREO:");
                e.printStackTrace();
            }
        } else {
            System.out.println("El usuario " + email + " NO existe en la memoria.");
        }
    }

    // 2. Validar token
    public boolean isValidToken(String token) {
        return resetTokens.containsKey(token);
    }

    // 3. Cambiar la contraseña
    public void changeUserPassword(String token, String newPassword) {
        String email = resetTokens.get(token);

        if (email != null && userDetailsManager.userExists(email)) {
            // Obtenemos el usuario actual
            UserDetails oldUser = userDetailsManager.loadUserByUsername(email);

            // Creamos uno nuevo con la contraseña actualizada pero los mismos roles
            UserDetails newUser = User.withUsername(oldUser.getUsername())
                    .password(passwordEncoder.encode(newPassword))
                    .authorities(oldUser.getAuthorities())
                    .build();

            // Actualizamos en el Manager en memoria
            userDetailsManager.updateUser(newUser);

            // Borramos el token para que no se pueda volver a usar
            resetTokens.remove(token);
        }
    }
}