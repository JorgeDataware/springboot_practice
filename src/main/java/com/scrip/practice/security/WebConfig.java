package com.scrip.practice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz)  -> {
            authz.requestMatchers("/").permitAll()
                    .anyRequest().authenticated();
        }
        ).logout((logout) -> logout.permitAll());

        return http.build();
    }
}
