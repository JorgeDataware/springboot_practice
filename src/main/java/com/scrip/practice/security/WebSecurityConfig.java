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
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(
                User.withUsername("user")
                .password(passwordEncoder().encode("1234"))
                .roles("USER")
                .build());

        manager.createUser(
            User.withUsername("coordinador")
                    .password(passwordEncoder().encode("coord"))
                    .roles("USER", "COORDINADOR")
                    .build());

        manager.createUser(
            User.withUsername("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("USER", "ADMIN")
                    .build());

        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // Ruta pública - accesible sin autenticación
                .requestMatchers("/").permitAll()
                // Rutas exclusivas de ADMIN - gestión de ofertas
                .requestMatchers("/consola/gestion_ofertas", "/api/ofertas", "/api/ofertas/**", "/ofertas/nueva", "/ofertas/nueva/**", "/ofertas/guardar", "/ofertas/eliminar/**").hasRole("ADMIN")
                // Rutas de gestión de divisiones - ADMIN o COORDINADOR
                .requestMatchers("/consola/gestion_divisiones", "/api/divisiones", "/api/divisiones/**", "/divisiones/guardar", "/divisiones/eliminar/**").hasAnyRole("ADMIN", "COORDINADOR")
                // Rutas de alumno - requieren autenticación (USER, COORDINADOR o ADMIN)
                .requestMatchers("/alumno/**").hasAnyRole("USER", "COORDINADOR", "ADMIN")
                // Otras rutas requieren autenticación
                .requestMatchers("/ofertas").authenticated()
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
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
