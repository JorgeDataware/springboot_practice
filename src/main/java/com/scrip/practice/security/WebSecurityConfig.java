// WebSecurityConfig.java
package com.scrip.practice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(
                User.withUsername("usuario@ejemplo.com")
                        .password(passwordEncoder().encode("1234"))
                        .roles("USER")
                        .build());

        manager.createUser(
                User.withUsername("coordinador@ejemplo.com")
                        .password(passwordEncoder().encode("coord"))
                        .roles("USER", "COORDINADOR")
                        .build());

        manager.createUser(
                User.withUsername("2023371055@uteq.edu.mx")
                        .password(passwordEncoder().encode("admin"))
                        .roles("USER", "ADMIN")
                        .build());

        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // 1. RUTAS PÚBLICAS
                        // Accesibles para cualquier persona (logueada o no)
                        .requestMatchers("/", "/login", "/error", "/forgot-password", "/reset-password").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Recursos estáticos

                        // 2. GESTIÓN DE IDENTIDAD (Solo ADMIN)
                        // El Coordinador y Alumno no pueden entrar aquí
                        .requestMatchers("/consola/identidad/**").hasRole("ADMIN")

                        // 3. GESTIÓN DE DIVISIONES (Solo ADMIN)
                        // Según tu instrucción: "Coordinador acceso a todo EXCEPTO identidad y divisiones"
                        .requestMatchers("/consola/gestion_divisiones/**", "/api/divisiones/**", "/divisiones/**").hasRole("ADMIN")

                        // 4. GESTIÓN DE OFERTAS (ADMIN y COORDINADOR)
                        // El coordinador puede gestionar las carreras, pero el alumno no.
                        .requestMatchers("/consola/gestion_ofertas/**", "/api/ofertas/**", "/ofertas/nueva/**", "/ofertas/guardar", "/ofertas/eliminar/**").hasAnyRole("ADMIN", "COORDINADOR")

                        // 5. RUTAS DE ALUMNO / CONSULTA (ADMIN, COORDINADOR y USER)
                        // Aquí es donde el alumno (USER) puede ver su información y la identidad académica pública
                        .requestMatchers("/alumno/**", "/ofertas").hasAnyRole("USER", "COORDINADOR", "ADMIN")

                        // 6. CUALQUIER OTRA RUTA
                        // Por seguridad, cualquier cosa que se nos haya escapado requiere estar autenticado
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/api/**") // Deshabilitar CSRF para API REST
                );

        return http.build();
    }
}
