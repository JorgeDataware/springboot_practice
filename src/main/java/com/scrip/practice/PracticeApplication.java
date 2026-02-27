package com.scrip.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point.
 * 
 * <p>Bootstraps the application with component scanning rooted at the
 * {@code com.scrip.practice} package. Auto-configures Spring MVC (Thymeleaf),
 * Spring Data JPA (PostgreSQL), validation, and WebFlux (WebClient).</p>
 * 
 * <p>Run with {@code ./gradlew bootRun} or execute this class directly from the IDE.</p>
 */
@SpringBootApplication
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }

}
