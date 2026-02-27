# AGENTS_GUIDE.md — Project Context for AI Agents

> **Purpose:** This file provides AI coding agents with a complete map of the project
> so they can understand the architecture, locate files, and make changes without
> needing to scan the entire codebase first.
>
> **Last updated:** 2026-02-26

---

## 1. Project Overview

| Property          | Value                                              |
|-------------------|----------------------------------------------------|
| **Name**          | `practice`                                         |
| **Framework**     | Spring Boot 4.0.2 (Java 21)                       |
| **Build tool**    | Gradle (single-module)                             |
| **Database**      | PostgreSQL 5432 (`practice_db`)                    |
| **ORM**           | Spring Data JPA / Hibernate (`ddl-auto=update`)    |
| **Template engine**| Thymeleaf (server-side rendered HTML)             |
| **CSS/JS**        | Bootstrap 5.3.3 (CDN) + vanilla JavaScript        |
| **Other libs**    | Lombok, Spring Validation, Spring WebFlux (WebClient) |
| **Domain**        | University educational offerings management (UTEQ) |

This is a server-side rendered CRUD application. There is **no frontend SPA** (no React/Angular/Vue). All pages are Thymeleaf templates served by Spring MVC controllers. The UI uses Bootstrap 5 for layout and components.

---

## 2. Directory Structure

```
practice/
├── build.gradle                          # Gradle build config (dependencies, plugins)
├── settings.gradle                       # Project name: "practice"
├── AGENTS_GUIDE.md                       # THIS FILE
├── src/
│   ├── main/
│   │   ├── java/com/scrip/practice/
│   │   │   ├── PracticeApplication.java          # @SpringBootApplication entry point
│   │   │   ├── Controllers/
│   │   │   │   ├── OfertaController.java         # Routes: /, /ofertas, /consola/gestion_ofertas, /ofertas/*
│   │   │   │   └── DivisionController.java       # Routes: /consola/gestion_divisiones, /divisiones/*
│   │   │   ├── Models/
│   │   │   │   ├── Division.java                 # JPA entity → table "division" (int PK)
│   │   │   │   ├── OfertaEducativa.java          # JPA entity → table "oferta_educativa" (UUID PK)
│   │   │   │   └── PerfilEgreso.java             # POJO (not JPA) — transient graduation profile
│   │   │   ├── Repositories/
│   │   │   │   ├── DivisionRepository.java       # JpaRepository<Division, Integer>
│   │   │   │   └── OfertaEducativaRepository.java# JpaRepository<OfertaEducativa, UUID> + custom queries
│   │   │   └── Services/
│   │   │       ├── DivisionService.java          # CRUD service for Division
│   │   │       └── OfertaEducativaService.java   # CRUD service for OfertaEducativa (@Transactional)
│   │   └── resources/
│   │       ├── application.properties            # DB connection, JPA config
│   │       ├── db/
│   │       │   ├── divisiones.sql                # Sample division data (4 rows)
│   │       │   ├── datos_prueba.sql              # Sample offering data (19 rows from UTEQ)
│   │       │   └── cleanup_liquibase.sql         # Utility: drop Liquibase tracking tables
│   │       ├── js/
│   │       │   └── modal-division.js             # Empty placeholder (JS is inline in templates)
│   │       └── templates/
│   │           ├── Fragments/
│   │           │   └── fragment1.html            # Shared navbar fragment (th:fragment="menu")
│   │           ├── Inicio.html                   # Home page (GET /)
│   │           ├── OfertaEducativa.html          # Public student card view (GET /ofertas)
│   │           ├── GestionOfertas.html           # Admin CRUD table + modal (GET /consola/gestion_ofertas)
│   │           ├── GestionDivisiones.html        # Admin CRUD table + modal (GET /consola/gestion_divisiones)
│   │           ├── NuevaOferta.html              # Standalone form page (GET /ofertas/nueva)
│   │           ├── NuevaDivision.html            # Standalone form page (legacy)
│   │           └── error/
│   │               └── 404.html                  # 404 error page (NOTE: misplaced from another project)
│   └── test/
│       └── java/com/scrip/practice/
│           └── PracticeApplicationTests.java     # Spring Boot context load test
```

---

## 3. Database Schema

Hibernate auto-generates the schema from JPA entities (`ddl-auto=update`).

### Table: `division`

| Column   | Type      | Constraints         | Notes                       |
|----------|-----------|---------------------|-----------------------------|
| `id`     | `integer` | PK, auto-increment  | `GenerationType.IDENTITY`   |
| `cve`    | `varchar` | NOT NULL             | Division code (e.g. "DTAI") |
| `name`   | `varchar` | NOT NULL             | Division name               |
| `active` | `boolean` |                     | Active flag                 |

### Table: `oferta_educativa`

| Column        | Type          | Constraints        | Notes                          |
|---------------|---------------|--------------------|---------------------------------|
| `id`          | `uuid`        | PK, auto-generated | `GenerationType.UUID`          |
| `nombre`      | `varchar(255)`| NOT NULL            | Offering name                  |
| `modalidad`   | `varchar(100)`|                    | Study modality                  |
| `image_url`   | `varchar(500)`|                    | URL to representative image     |
| `division_id` | `integer`     | FK → division(id), nullable | ManyToOne, LAZY loaded |

### Relationship

```
Division (1) ←——→ (N) OfertaEducativa
         OneToMany      ManyToOne
         cascade=ALL    fetch=LAZY
         orphanRemoval=true
```

**IMPORTANT:** Deleting a Division cascades to delete ALL its OfertaEducativa entries.

---

## 4. Route Map

### OfertaController (`Controllers/OfertaController.java`)

| Method | URL                            | Purpose                                  | Template            | Model attributes              |
|--------|--------------------------------|------------------------------------------|---------------------|-------------------------------|
| GET    | `/`                            | Home page                                | `Inicio`            | (none)                        |
| GET    | `/ofertas`                     | Public student view (cards)              | `OfertaEducativa`   | `ofertas`                     |
| GET    | `/consola/gestion_ofertas`     | Admin management table + modal           | `GestionOfertas`    | `ofertas`, `divisiones`       |
| GET    | `/ofertas/nueva`               | Standalone new offering form             | `NuevaOferta`       | `divisiones`                  |
| GET    | `/ofertas/nueva/{id}`          | Standalone edit offering form            | `NuevaOferta`       | `oferta`, `divisiones`        |
| POST   | `/ofertas/guardar`             | Create or update offering (PRG)          | redirect            | flash: `mensaje` or `error`   |
| POST   | `/ofertas/eliminar/{id}`       | Delete offering (PRG)                    | redirect            | flash: `mensaje` or `error`   |

### DivisionController (`Controllers/DivisionController.java`)

| Method | URL                              | Purpose                         | Template              | Model attributes       |
|--------|----------------------------------|---------------------------------|-----------------------|------------------------|
| GET    | `/consola/gestion_divisiones`    | Admin management table + modal  | `GestionDivisiones`   | `divisiones`           |
| POST   | `/divisiones/guardar`            | Create or update division (PRG) | redirect              | flash: `mensaje`/`error` |
| POST   | `/divisiones/eliminar/{id}`      | Delete division (PRG)           | redirect              | flash: `mensaje`/`error` |

---

## 5. Architecture Patterns

### 5.1 Layered Architecture

```
Controller  →  Service  →  Repository  →  Database
    ↕              ↕            ↕
  Model         Model        Model
    ↕
 Template (Thymeleaf)
```

- **Controllers** handle HTTP requests, extract `@RequestParam`/`@PathVariable`, call services, populate the model, and return template names or redirects.
- **Services** contain business logic and delegate persistence to repositories. `OfertaEducativaService` is `@Transactional` at the class level.
- **Repositories** extend `JpaRepository` for standard CRUD. `OfertaEducativaRepository` has additional derived, JPQL, and native queries.
- **Models** are JPA entities annotated with Lombok `@Data` for auto-generated getters/setters.

### 5.2 CRUD Pattern (Modal-Based)

Both Division and OfertaEducativa management pages follow the same pattern:

1. **Single page** with a data table listing all records.
2. **Bootstrap 5 modal dialog** shared for both create and edit operations.
3. **Two JavaScript functions** control the modal:
   - `abrirModalNueva()` — resets form fields, sets title to "Nueva..."
   - `abrirModalEditar(btn)` — reads `data-*` attributes from the clicked button, populates form fields, sets title to "Editar..."
4. **Edit buttons** in each table row carry entity data as `th:data-*` HTML attributes.
5. **Form POST** to a single `/guardar` endpoint — determines create vs. update by presence of the `id` field.
6. **Hidden `id` input** is disabled for new records (so it's not submitted), enabled for edits.
7. **PRG pattern** (Post-Redirect-Get) with flash attributes for success/error messages.
8. **Delete** uses a separate POST form with `confirm()` dialog.

### 5.3 Create vs. Update Detection

The `/guardar` endpoints distinguish create from update by checking if the `id` request parameter is present:
- `id == null` → **create** a new entity
- `id != null` → **update** the existing entity with that ID

In the modal JavaScript, the hidden `id` input is **disabled** for new records (preventing it from being submitted) and **enabled** for edits.

---

## 6. Key File Reference

### Java Classes

| File | Path | Description |
|------|------|-------------|
| `PracticeApplication.java` | `src/main/java/com/scrip/practice/PracticeApplication.java` | Spring Boot entry point |
| `Division.java` | `src/main/java/com/scrip/practice/Models/Division.java` | Division JPA entity (int PK) |
| `OfertaEducativa.java` | `src/main/java/com/scrip/practice/Models/OfertaEducativa.java` | Educational offering JPA entity (UUID PK) |
| `PerfilEgreso.java` | `src/main/java/com/scrip/practice/Models/PerfilEgreso.java` | Graduation profile POJO (transient, not persisted) |
| `DivisionRepository.java` | `src/main/java/com/scrip/practice/Repositories/DivisionRepository.java` | JpaRepository for Division (basic CRUD only) |
| `OfertaEducativaRepository.java` | `src/main/java/com/scrip/practice/Repositories/OfertaEducativaRepository.java` | JpaRepository for OfertaEducativa (with custom queries) |
| `DivisionService.java` | `src/main/java/com/scrip/practice/Services/DivisionService.java` | Division CRUD service |
| `OfertaEducativaService.java` | `src/main/java/com/scrip/practice/Services/OfertaEducativaService.java` | OfertaEducativa CRUD service (@Transactional) |
| `DivisionController.java` | `src/main/java/com/scrip/practice/Controllers/DivisionController.java` | Division routes controller |
| `OfertaController.java` | `src/main/java/com/scrip/practice/Controllers/OfertaController.java` | Offering routes + home page controller |

### Templates (Thymeleaf)

| File | Path | Renders at | Purpose |
|------|------|------------|---------|
| `fragment1.html` | `src/main/resources/templates/Fragments/fragment1.html` | (fragment) | Shared navbar — included via `th:replace` |
| `Inicio.html` | `src/main/resources/templates/Inicio.html` | `GET /` | Home page with navigation cards |
| `OfertaEducativa.html` | `src/main/resources/templates/OfertaEducativa.html` | `GET /ofertas` | Public student card view |
| `GestionOfertas.html` | `src/main/resources/templates/GestionOfertas.html` | `GET /consola/gestion_ofertas` | Admin table + create/edit modal for offerings |
| `GestionDivisiones.html` | `src/main/resources/templates/GestionDivisiones.html` | `GET /consola/gestion_divisiones` | Admin table + create/edit modal for divisions |
| `NuevaOferta.html` | `src/main/resources/templates/NuevaOferta.html` | `GET /ofertas/nueva[/{id}]` | Standalone form page for offerings |
| `NuevaDivision.html` | `src/main/resources/templates/NuevaDivision.html` | (legacy) | Standalone form page for divisions |
| `404.html` | `src/main/resources/templates/error/404.html` | (auto) | Error page (NOTE: content is from a different project) |

### Configuration & SQL

| File | Path | Purpose |
|------|------|---------|
| `build.gradle` | `build.gradle` | Dependencies and build config |
| `application.properties` | `src/main/resources/application.properties` | DB connection, JPA settings |
| `divisiones.sql` | `src/main/resources/db/divisiones.sql` | Sample division seed data |
| `datos_prueba.sql` | `src/main/resources/db/datos_prueba.sql` | Sample offering seed data (19 UTEQ programs) |
| `cleanup_liquibase.sql` | `src/main/resources/db/cleanup_liquibase.sql` | Utility to drop Liquibase tables |

---

## 7. Entity Field Reference

### Division

| Java field | DB column | Type | Validation | Notes |
|------------|-----------|------|------------|-------|
| `Id` | `id` | `int` | auto-increment | PK |
| `cve` | `cve` | `String` | `@NotEmpty` | Division code |
| `name` | `name` | `String` | `@NotEmpty` | Division name |
| `active` | `active` | `boolean` | — | Active flag |
| `OfertasEducativas` | — | `List<OfertaEducativa>` | — | OneToMany, cascade ALL, orphan removal |

### OfertaEducativa

| Java field | DB column | Type | Validation | Notes |
|------------|-----------|------|------------|-------|
| `id` | `id` | `UUID` | auto-generated | PK |
| `nombre` | `nombre` | `String` | NOT NULL, max 255 | Offering name |
| `modalidad` | `modalidad` | `String` | max 100 | Study modality |
| `imageUrl` | `image_url` | `String` | max 500 | Image URL |
| `division` | `division_id` | `Division` | nullable FK | ManyToOne, LAZY |
| `perfil` | — | `PerfilEgreso` | `@Transient` | Not persisted |

### PerfilEgreso (POJO, not persisted)

| Field | Type | Notes |
|-------|------|-------|
| `Id` | `UUID` | Identifier |
| `Description` | `String` | Profile description |
| `HabilidadesTransversales` | `List<String>` | Soft/general skills |
| `HabilidadesEspecificas` | `List<String>` | Technical/domain skills |

---

## 8. Available Modalities (Hardcoded in Templates)

The modality dropdown in the offering forms contains these options:

1. `Modalidad intensiva y mixta`
2. `Modalidad vespertina y mixta`
3. `Modalidad presencial`
4. `Modalidad en línea`
5. `Modalidad híbrida`

These values are hardcoded in the HTML templates (`GestionOfertas.html`, `NuevaOferta.html`). They are **not** stored in a separate database table.

---

## 9. Frontend Patterns

### Shared Navbar

All pages include the navbar via Thymeleaf fragment inclusion:
```html
<nav th:replace="Fragments/fragment1 :: menu"></nav>
```

The navbar links to: Home (`/`), Ofertas Educativas (`/ofertas`), Gestionar Ofertas (`/consola/gestion_ofertas`), Gestionar Divisiones (`/consola/gestion_divisiones`).

### Modal CRUD Pattern (used by both Divisions and Offerings)

The management pages (`GestionDivisiones.html`, `GestionOfertas.html`) use inline Bootstrap 5 modals for create/edit. The pattern is:

```
[New Button] → onclick="abrirModalNueva()" + data-bs-toggle="modal"
    ↓
Modal opens with empty form (id input disabled)
    ↓
User fills form → POST /entity/guardar (id is not sent)
    ↓
Controller: id==null → create new entity
    ↓
Redirect back to management page with flash message

[Edit Button] → onclick="abrirModalEditar(this)" + data-bs-toggle="modal"
                (carries data-* attributes with entity values)
    ↓
Modal opens with pre-filled form (id input enabled)
    ↓
User modifies form → POST /entity/guardar (id IS sent)
    ↓
Controller: id!=null → update existing entity
    ↓
Redirect back to management page with flash message
```

### Flash Messages

Success and error messages are passed via Spring's `RedirectAttributes.addFlashAttribute()` and displayed in the templates:

```html
<div th:if="${mensaje}" class="alert alert-success ...">
<div th:if="${error}" class="alert alert-danger ...">
```

---

## 10. How to Run

### Prerequisites
- Java 21
- PostgreSQL running on `localhost:5432`
- Database `practice_db` created

### Commands

```bash
# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Build JAR
./gradlew build
```

The application starts at `http://localhost:8080` by default.

### Database Setup

The schema is auto-created by Hibernate (`ddl-auto=update`). To seed sample data, manually run:
- `src/main/resources/db/divisiones.sql` — 4 sample divisions
- `src/main/resources/db/datos_prueba.sql` — 19 sample educational offerings

---

## 11. Known Issues & Technical Debt

1. **404 page mismatch:** `templates/error/404.html` contains HTML from a different project ("PRIMEFORCE" e-commerce). It references fragments (`fragments/navbar`, `fragments/footer`) that do not exist in this project. It should be replaced.

2. **Credentials in properties:** `application.properties` contains a database password in plain text. Consider using environment variables or Spring profiles.

3. **Cascade delete risk:** Deleting a `Division` cascades to delete ALL its `OfertaEducativa` entries due to `CascadeType.ALL` + `orphanRemoval = true`. The UI has a `confirm()` dialog but no server-side warning about affected offerings.

4. **Empty JS file:** `src/main/resources/js/modal-division.js` exists but is empty. The modal JavaScript is inline in the templates instead.

5. **Standalone form pages:** `NuevaOferta.html` and `NuevaDivision.html` are standalone form pages that duplicate the modal functionality. The management pages now use modals for create/edit, making these pages partially redundant (though they still have working routes).

6. **PascalCase fields:** `PerfilEgreso` uses PascalCase public fields (`Id`, `Description`, etc.) which deviates from Java naming conventions.

7. **No authentication:** There is no login/auth system. All admin routes (`/consola/*`) are publicly accessible.

---

## 12. Adding a New Entity (Step-by-Step Guide)

To add a new CRUD entity following the existing patterns:

1. **Model:** Create a JPA entity class in `Models/` with `@Data`, `@Entity`, `@Table`.
2. **Repository:** Create an interface in `Repositories/` extending `JpaRepository<YourEntity, PKType>`.
3. **Service:** Create a service class in `Services/` with `obtenerTodas()`, `obtenerPorId()`, `crear()`, `actualizar()`, `eliminar()` methods.
4. **Controller:** Create a controller in `Controllers/` with:
   - `GET /consola/gestion_<plural>` — management page (pass entity list + any related lists to model)
   - `POST /<plural>/guardar` — create/update (check if `id` param is null for create vs. update)
   - `POST /<plural>/eliminar/{id}` — delete
5. **Template:** Create a Thymeleaf template in `templates/` with:
   - Include navbar fragment: `<nav th:replace="Fragments/fragment1 :: menu"></nav>`
   - Data table with `th:each`
   - Bootstrap modal with form (action=`/plural/guardar`)
   - JavaScript functions `abrirModalNueva()` and `abrirModalEditar(btn)`
   - Flash message alerts for `${mensaje}` and `${error}`
6. **Navbar:** Add a link to the new management page in `Fragments/fragment1.html`.

---

## 13. Repository Custom Query Reference

`OfertaEducativaRepository` has these custom query methods beyond basic CRUD:

| Method | Type | Description |
|--------|------|-------------|
| `findByModalidad(String)` | Derived | Find by exact modality |
| `findByNombre(String)` | Derived | Find by exact name |
| `findByNombreContainingIgnoreCase(String)` | Derived | Case-insensitive name search |
| `findByModalidadOrderByNombreAsc(String)` | Derived | Find by modality, sorted by name |
| `existsByNombre(String)` | Derived | Check if name exists |
| `buscarPorTexto(String)` | JPQL | Search nombre OR modalidad (case-insensitive LIKE) |
| `contarPorModalidad(String)` | JPQL | Count offerings by modality |
| `obtenerPorModalidadConLimite(String, int)` | Native SQL | Find by modality with PostgreSQL LIMIT |

`DivisionRepository` has **no custom methods** — only the inherited `JpaRepository` CRUD methods.
