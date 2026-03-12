# AGENTS_GUIDE.md — Project Context for AI Agents

> **Purpose:** This file provides AI coding agents with a complete map of the project
> so they can understand the architecture, locate files, and make changes without
> needing to scan the entire codebase first.
>
> **Last updated:** 2026-03-11 (Security implementation with roles)

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
| **Security**      | Spring Security with role-based access control     |
| **Other libs**    | Lombok, Spring Validation, Spring WebFlux (WebClient), Thymeleaf Security Extras |
| **Domain**        | University educational offerings management (UTEQ) |

This is a server-side rendered CRUD application. There is **no frontend SPA** (no React/Angular/Vue). All pages are Thymeleaf templates served by Spring MVC controllers. The UI uses Bootstrap 5 for layout and components. The admin management pages (`GestionDivisiones.html`, `GestionOfertas.html`) use **AJAX (fetch API)** to perform create/update/delete operations without page reloads, communicating with dedicated REST API controllers that return JSON.

**Authentication & Authorization:** The application uses Spring Security with three user roles (`USER`, `COORDINADOR`, `ADMIN`) and form-based login. Routes are protected based on roles, and the UI dynamically shows/hides elements using Thymeleaf Security expressions.

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
│   │   │   │   ├── OfertaController.java         # MVC routes: /, /ofertas, /consola/gestion_ofertas, /ofertas/*
│   │   │   │   ├── DivisionController.java       # MVC routes: /consola/gestion_divisiones, /divisiones/*
│   │   │   │   ├── AlumnoController.java         # MVC routes: /alumno/* (admisiones, valores, mapa-sitio)
│   │   │   │   ├── OfertaApiController.java      # REST API: /api/ofertas (JSON CRUD, no page reload)
│   │   │   │   └── DivisionApiController.java    # REST API: /api/divisiones (JSON CRUD, no page reload)
│   │   │   ├── security/
│   │   │   │   └── WebSecurityConfig.java        # Spring Security configuration (users, roles, permissions)
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
│   │           │   └── fragment1.html            # Shared navbar fragment (th:fragment="menu") with role-based menu items
│   │           ├── Inicio.html                   # Home page (GET /) — public access
│   │           ├── OfertaEducativa.html          # Public student card view (GET /ofertas) — requires login
│   │           ├── GestionOfertas.html           # Admin CRUD table + modal (AJAX, no page reload) — ADMIN only
│   │           ├── GestionDivisiones.html        # Admin/Coordinator CRUD table + modal (AJAX, no page reload) — ADMIN or COORDINADOR
│   │           ├── NuevaOferta.html              # Standalone form page (GET /ofertas/nueva) — ADMIN only
│   │           ├── NuevaDivision.html            # Standalone form page (legacy)
│   │           ├── Admisiones.html               # Admissions page (GET /alumno/admisiones) — requires login
│   │           ├── Valores.html                  # Institutional values page (GET /alumno/valores) — requires login
│   │           ├── MapaSitio.html                # Site map page (GET /alumno/mapa-sitio) — requires login
│   │           └── error/
│   │               ├── 403.html                  # 403 Forbidden error page (custom)
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

## 4. Security & Authentication

**Implementation date:** 2026-03-11

The application uses **Spring Security** with form-based login and role-based access control (RBAC). Authentication is required for most routes, with different permission levels based on user roles.

### 4.1 User Roles

| Role | Username | Password | Access Level | Description |
|------|----------|----------|--------------|-------------|
| `USER` | `user` | `1234` | Basic | Can view offerings and access student portal pages |
| `COORDINADOR` | `coordinador` | `coord` | Division Management | Can manage divisions only (create, edit, delete) |
| `ADMIN` | `admin` | `admin` | Full Access | Can manage both offerings and divisions |

**Notes:**
- All users have the base `USER` role
- `COORDINADOR` users have both `USER` and `COORDINADOR` roles
- `ADMIN` users have both `USER` and `ADMIN` roles
- Passwords are encrypted with `BCryptPasswordEncoder`
- Users are stored in-memory (not in the database)

### 4.2 Route Authorization Matrix

| Route Pattern | Public | USER | COORDINADOR | ADMIN | Notes |
|--------------|--------|------|-------------|-------|-------|
| `/` | ✅ | ✅ | ✅ | ✅ | Home page (no login required) |
| `/login` | ✅ | ✅ | ✅ | ✅ | Spring Security default login page |
| `/logout` | ✅ | ✅ | ✅ | ✅ | Logout endpoint (POST only) |
| `/ofertas` | ❌ | ✅ | ✅ | ✅ | View educational offerings |
| `/alumno/**` | ❌ | ✅ | ✅ | ✅ | Student portal pages |
| `/alumno/admisiones` | ❌ | ✅ | ✅ | ✅ | Admissions page |
| `/alumno/valores` | ❌ | ✅ | ✅ | ✅ | Institutional values |
| `/alumno/mapa-sitio` | ❌ | ✅ | ✅ | ✅ | Site map |
| `/consola/gestion_divisiones` | ❌ | ❌ | ✅ | ✅ | Division management console |
| `/api/divisiones/**` | ❌ | ❌ | ✅ | ✅ | Division REST API |
| `/divisiones/guardar` | ❌ | ❌ | ✅ | ✅ | Legacy division save endpoint |
| `/divisiones/eliminar/**` | ❌ | ❌ | ✅ | ✅ | Legacy division delete endpoint |
| `/consola/gestion_ofertas` | ❌ | ❌ | ❌ | ✅ | Offering management console |
| `/api/ofertas/**` | ❌ | ❌ | ❌ | ✅ | Offering REST API |
| `/ofertas/nueva/**` | ❌ | ❌ | ❌ | ✅ | Standalone offering form |
| `/ofertas/guardar` | ❌ | ❌ | ❌ | ✅ | Legacy offering save endpoint |
| `/ofertas/eliminar/**` | ❌ | ❌ | ❌ | ✅ | Legacy offering delete endpoint |

### 4.3 Security Configuration

**File:** `src/main/java/com/scrip/practice/security/WebSecurityConfig.java`

Key configuration points:
- `BCryptPasswordEncoder` for password hashing
- `InMemoryUserDetailsManager` for user storage (3 predefined users)
- `SecurityFilterChain` with granular `requestMatchers()` for route protection
- Form login with default success URL: `/`
- Logout redirects to: `/`
- CSRF protection disabled for `/api/**` endpoints (required for AJAX REST calls)

### 4.4 UI Security Features

The navbar and pages dynamically show/hide elements based on the authenticated user's role using Thymeleaf Security expressions:

**Thymeleaf Security Namespace:**
```xml
xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
```

**Common Security Expressions:**
- `sec:authorize="isAuthenticated()"` — Show only to logged-in users
- `sec:authorize="!isAuthenticated()"` — Show only to anonymous users
- `sec:authorize="hasRole('ADMIN')"` — Show only to ADMIN
- `sec:authorize="hasRole('COORDINADOR')"` — Show only to COORDINADOR
- `sec:authorize="hasAnyRole('ADMIN', 'COORDINADOR')"` — Show to either role
- `sec:authentication="name"` — Display the logged-in username

**Role Badges in Navbar:**
- **ADMIN**: Blue badge (`bg-primary`) — "Admin"
- **COORDINADOR**: Cyan badge (`bg-info`) — "Coordinador"
- **USER**: Gray badge (`bg-secondary`) — "Usuario"

### 4.5 Error Pages

- **403 Forbidden** (`templates/error/403.html`): Custom error page displayed when a user attempts to access a route they don't have permission for. Shows current user role and provides navigation buttons.
- **404 Not Found** (`templates/error/404.html`): Exists but contains misplaced content from another project.

### 4.6 Login Flow

1. Anonymous user attempts to access a protected route (e.g., `/ofertas`)
2. Spring Security intercepts and redirects to `/login`
3. User enters credentials
4. On success, redirected to `/` (home page)
5. On logout, redirected back to `/`
6. Users can navigate freely between authorized pages without re-login

---

## 5. Route Map

### OfertaController (`Controllers/OfertaController.java`) — MVC (page rendering)

| Method | URL                            | Purpose                                  | Template            | Model attributes              | Authorization |
|--------|--------------------------------|------------------------------------------|---------------------|-------------------------------|---------------|
| GET    | `/`                            | Home page                                | `Inicio`            | (none)                        | Public        |
| GET    | `/ofertas`                     | Public student view (cards)              | `OfertaEducativa`   | `ofertas`                     | Authenticated |
| GET    | `/consola/gestion_ofertas`     | Admin management table + modal           | `GestionOfertas`    | `ofertas`, `divisiones`       | ADMIN only    |
| GET    | `/ofertas/nueva`               | Standalone new offering form             | `NuevaOferta`       | `divisiones`                  | ADMIN only    |
| GET    | `/ofertas/nueva/{id}`          | Standalone edit offering form            | `NuevaOferta`       | `oferta`, `divisiones`        | ADMIN only    |
| POST   | `/ofertas/guardar`             | Create or update offering (PRG, legacy)  | redirect            | flash: `mensaje` or `error`   | ADMIN only    |
| POST   | `/ofertas/eliminar/{id}`       | Delete offering (PRG, legacy)            | redirect            | flash: `mensaje` or `error`   | ADMIN only    |

### OfertaApiController (`Controllers/OfertaApiController.java`) — REST API (JSON, used by AJAX)

| Method | URL                    | Purpose                        | Request body (JSON)                                   | Response (JSON)                          | Authorization |
|--------|------------------------|--------------------------------|-------------------------------------------------------|------------------------------------------|---------------|
| GET    | `/api/ofertas`         | List all offerings             | —                                                     | `[ { id, nombre, modalidad, ... } ]`     | ADMIN only    |
| POST   | `/api/ofertas`         | Create a new offering          | `{ nombre, modalidad, imageUrl, divisionId }`          | `{ success, mensaje, oferta }`           | ADMIN only    |
| PUT    | `/api/ofertas/{id}`    | Update an existing offering    | `{ nombre, modalidad, imageUrl, divisionId }`          | `{ success, mensaje, oferta }`           | ADMIN only    |
| DELETE | `/api/ofertas/{id}`    | Delete an offering             | —                                                     | `{ success, mensaje }`                   | ADMIN only    |

### DivisionController (`Controllers/DivisionController.java`) — MVC (page rendering)

| Method | URL                              | Purpose                              | Template              | Model attributes       | Authorization |
|--------|----------------------------------|--------------------------------------|-----------------------|------------------------|---------------|
| GET    | `/consola/gestion_divisiones`    | Admin/Coordinator management table + modal | `GestionDivisiones`   | `divisiones`           | ADMIN or COORDINADOR |
| POST   | `/divisiones/guardar`            | Create or update division (PRG, legacy) | redirect           | flash: `mensaje`/`error` | ADMIN or COORDINADOR |
| POST   | `/divisiones/eliminar/{id}`      | Delete division (PRG, legacy)        | redirect              | flash: `mensaje`/`error` | ADMIN or COORDINADOR |

### DivisionApiController (`Controllers/DivisionApiController.java`) — REST API (JSON, used by AJAX)

| Method | URL                       | Purpose                        | Request body (JSON)              | Response (JSON)                          | Authorization |
|--------|---------------------------|--------------------------------|----------------------------------|------------------------------------------|---------------|
| GET    | `/api/divisiones`         | List all divisions             | —                                | `[ { id, cve, name, active } ]`          | ADMIN or COORDINADOR |
| POST   | `/api/divisiones`         | Create a new division          | `{ cve, name, active }`          | `{ success, mensaje, division }`         | ADMIN or COORDINADOR |
| PUT    | `/api/divisiones/{id}`    | Update an existing division    | `{ cve, name, active }`          | `{ success, mensaje, division }`         | ADMIN or COORDINADOR |
| DELETE | `/api/divisiones/{id}`    | Delete a division              | —                                | `{ success, mensaje }`                   | ADMIN or COORDINADOR |

### AlumnoController (`Controllers/AlumnoController.java`) — MVC (student portal pages)

| Method | URL                       | Purpose                        | Template              | Model attributes       | Authorization |
|--------|---------------------------|--------------------------------|-----------------------|------------------------|---------------|
| GET    | `/alumno/admisiones`      | Admissions information page    | `Admisiones`          | (none)                 | Authenticated (USER, COORDINADOR, or ADMIN) |
| GET    | `/alumno/valores`         | Institutional values page      | `Valores`             | `valores` (List)       | Authenticated (USER, COORDINADOR, or ADMIN) |
| GET    | `/alumno/mapa-sitio`      | Site map navigation page       | `MapaSitio`           | (none)                 | Authenticated (USER, COORDINADOR, or ADMIN) |

> **Note:** The MVC POST routes (`/ofertas/guardar`, `/divisiones/guardar`, etc.) still exist for backward compatibility
> (e.g. the standalone form pages `NuevaOferta.html`), but the admin management pages now use the REST API endpoints exclusively.

---

## 5. Architecture Patterns

### 5.1 Layered Architecture

```
MVC Controller  →  Service  →  Repository  →  Database
     ↕                ↕            ↕
   Model            Model        Model
     ↕
  Template (Thymeleaf)

API Controller  →  Service  →  Repository  →  Database
     ↕                ↕            ↕
   JSON             Model        Model
```

- **MVC Controllers** (`@Controller`) handle page rendering — extract `@RequestParam`/`@PathVariable`, call services, populate the model, and return template names. They also have legacy PRG POST endpoints.
- **API Controllers** (`@RestController`) handle AJAX requests from the management pages — accept JSON request bodies, call services, and return JSON responses. They use a `toMap()` helper to serialize entities without circular reference issues.
- **Services** contain business logic and delegate persistence to repositories. `OfertaEducativaService` is `@Transactional` at the class level.
- **Repositories** extend `JpaRepository` for standard CRUD. `OfertaEducativaRepository` has additional derived, JPQL, and native queries.
- **Models** are JPA entities annotated with Lombok `@Data` for auto-generated getters/setters.

### 5.2 CRUD Pattern (Modal-Based + AJAX)

Both Division and OfertaEducativa management pages follow the same pattern:

1. **Single page** with a data table listing all records (initial render via Thymeleaf `th:each`).
2. **Bootstrap 5 modal dialog** shared for both create and edit operations.
3. **Two JavaScript functions** control the modal:
   - `abrirModalNueva()` — resets form fields, sets title to "Nueva..."
   - `abrirModalEditar(btn)` — reads `data-*` attributes from the clicked button, populates form fields, sets title to "Editar..."
4. **Edit buttons** in each table row carry entity data as `th:data-*` HTML attributes.
5. **Form submit intercepted** via `e.preventDefault()` — sends JSON to REST API via `fetch()` instead of a traditional POST.
6. **Hidden `id` input** determines create vs. update: disabled → POST to `/api/<entity>` (create), enabled → PUT to `/api/<entity>/{id}` (update).
7. **DOM updated in-place** after successful API response — new rows are appended, existing rows are updated, no page reload.
8. **Delete** uses a `confirm()` dialog, then sends `DELETE` via `fetch()` and removes the row from the DOM.
9. **Dynamic alerts** shown via `mostrarAlerta()` function (Bootstrap alert, auto-dismiss after 5 seconds).
10. **Table rows** have IDs (`fila-{id}`) and CSS classes on cells (`col-id`, `col-cve`, etc.) to enable targeted DOM updates.

### 5.3 Create vs. Update Detection

The REST API endpoints use HTTP method semantics:
- `POST /api/<entity>` → **create** a new entity
- `PUT /api/<entity>/{id}` → **update** the existing entity with that ID

In the modal JavaScript, the hidden `id` input is **disabled** for new records and **enabled** for edits. The form submit handler checks `idField.disabled` to decide which HTTP method and URL to use.

The legacy MVC `/guardar` endpoints (still present) distinguish create from update by checking if the `id` request parameter is present (`id == null` → create, `id != null` → update).

---

## 6. Key File Reference

### Java Classes

| File | Path | Description |
|------|------|-------------|
| `PracticeApplication.java` | `src/main/java/com/scrip/practice/PracticeApplication.java` | Spring Boot entry point |
| `WebSecurityConfig.java` | `src/main/java/com/scrip/practice/security/WebSecurityConfig.java` | Spring Security configuration (users, roles, permissions) |
| `Division.java` | `src/main/java/com/scrip/practice/Models/Division.java` | Division JPA entity (int PK) |
| `OfertaEducativa.java` | `src/main/java/com/scrip/practice/Models/OfertaEducativa.java` | Educational offering JPA entity (UUID PK) |
| `PerfilEgreso.java` | `src/main/java/com/scrip/practice/Models/PerfilEgreso.java` | Graduation profile POJO (transient, not persisted) |
| `DivisionRepository.java` | `src/main/java/com/scrip/practice/Repositories/DivisionRepository.java` | JpaRepository for Division (basic CRUD only) |
| `OfertaEducativaRepository.java` | `src/main/java/com/scrip/practice/Repositories/OfertaEducativaRepository.java` | JpaRepository for OfertaEducativa (with custom queries) |
| `DivisionService.java` | `src/main/java/com/scrip/practice/Services/DivisionService.java` | Division CRUD service |
| `OfertaEducativaService.java` | `src/main/java/com/scrip/practice/Services/OfertaEducativaService.java` | OfertaEducativa CRUD service (@Transactional) |
| `DivisionController.java` | `src/main/java/com/scrip/practice/Controllers/DivisionController.java` | Division MVC routes (page rendering + legacy PRG) |
| `OfertaController.java` | `src/main/java/com/scrip/practice/Controllers/OfertaController.java` | Offering MVC routes + home page |
| `AlumnoController.java` | `src/main/java/com/scrip/practice/Controllers/AlumnoController.java` | Student portal MVC routes (admisiones, valores, mapa-sitio) |
| `DivisionApiController.java` | `src/main/java/com/scrip/practice/Controllers/DivisionApiController.java` | Division REST API — JSON CRUD for AJAX |
| `OfertaApiController.java` | `src/main/java/com/scrip/practice/Controllers/OfertaApiController.java` | Offering REST API — JSON CRUD for AJAX |

### Templates (Thymeleaf)

| File | Path | Renders at | Purpose | Authorization |
|------|------|------------|---------|---------------|
| `fragment1.html` | `src/main/resources/templates/Fragments/fragment1.html` | (fragment) | Shared navbar with role-based menu items, logout button | All pages |
| `Inicio.html` | `src/main/resources/templates/Inicio.html` | `GET /` | Home page with navigation cards | Public |
| `OfertaEducativa.html` | `src/main/resources/templates/OfertaEducativa.html` | `GET /ofertas` | Public student card view | Authenticated |
| `Admisiones.html` | `src/main/resources/templates/Admisiones.html` | `GET /alumno/admisiones` | Admissions information page (placeholder) | Authenticated |
| `Valores.html` | `src/main/resources/templates/Valores.html` | `GET /alumno/valores` | Institutional values page | Authenticated |
| `MapaSitio.html` | `src/main/resources/templates/MapaSitio.html` | `GET /alumno/mapa-sitio` | Site map with role-based sections | Authenticated |
| `GestionOfertas.html` | `src/main/resources/templates/GestionOfertas.html` | `GET /consola/gestion_ofertas` | Admin table + AJAX create/edit/delete modal for offerings | ADMIN only |
| `GestionDivisiones.html` | `src/main/resources/templates/GestionDivisiones.html` | `GET /consola/gestion_divisiones` | Admin/Coordinator table + AJAX modal for divisions | ADMIN or COORDINADOR |
| `NuevaOferta.html` | `src/main/resources/templates/NuevaOferta.html` | `GET /ofertas/nueva[/{id}]` | Standalone form page for offerings | ADMIN only |
| `NuevaDivision.html` | `src/main/resources/templates/NuevaDivision.html` | (legacy) | Standalone form page for divisions | ADMIN or COORDINADOR |
| `403.html` | `src/main/resources/templates/error/403.html` | (auto) | Custom 403 Forbidden error page with role badges | All authenticated |
| `403.html` | `src/main/resources/templates/error/403.html` | (auto) | Custom 403 Forbidden error page with role badges | All authenticated |
| `404.html` | `src/main/resources/templates/error/404.html` | (auto) | Error page (NOTE: content is from a different project) | All |

### Configuration & SQL

| File | Path | Purpose |
|------|------|---------|
| `build.gradle` | `build.gradle` | Dependencies and build config (includes Spring Security + Thymeleaf Security Extras) |
| `application.properties` | `src/main/resources/application.properties` | DB connection, JPA settings |
| `divisiones.sql` | `src/main/resources/db/divisiones.sql` | Sample division seed data |
| `datos_prueba.sql` | `src/main/resources/db/datos_prueba.sql` | Sample offering seed data (19 UTEQ programs) |
| `cleanup_liquibase.sql` | `src/main/resources/db/cleanup_liquibase.sql` | Utility to drop Liquibase tables |

**Key build.gradle dependencies:**
- `spring-boot-starter-security` — Authentication and authorization
- `thymeleaf-extras-springsecurity6` — Thymeleaf security expressions (`sec:authorize`, `sec:authentication`, etc.)

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

**Navbar features:**
- **Public links** (always visible): Home (`/`)
- **Authenticated links** (visible when logged in): 
  - Ofertas Educativas (`/ofertas`)
  - **Alumno dropdown menu**: Admisiones, Valores Institucionales, Mapa del Sitio
- **Coordinator links** (visible for `COORDINADOR` and `ADMIN`): Gestionar Divisiones (`/consola/gestion_divisiones`)
- **Admin-only links** (visible for `ADMIN` only): Gestionar Ofertas (`/consola/gestion_ofertas`)
- **User info** (visible when logged in): Username with role badge (Admin/Coordinador/Usuario)
- **Authentication buttons**:
  - "Iniciar sesión" button (visible when NOT logged in)
  - "Cerrar sesión" button (visible when logged in)

All menu items use Thymeleaf Security expressions to conditionally render based on authentication status and user roles.

### Modal CRUD Pattern (used by both Divisions and Offerings)

The management pages (`GestionDivisiones.html`, `GestionOfertas.html`) use inline Bootstrap 5 modals for create/edit. All operations are performed via **AJAX (fetch API)** — no page reloads occur. The pattern is:

```
[New Button] → onclick="abrirModalNueva()" + data-bs-toggle="modal"
    ↓
Modal opens with empty form (id input disabled)
    ↓
User fills form → submit intercepted via e.preventDefault()
    ↓
fetch() sends POST /api/<entity> with JSON body
    ↓
API returns { success, mensaje, entity }
    ↓
JS creates new <tr> and appends to table via crearFila<Entity>()
    ↓
Modal closes, success alert shown via mostrarAlerta()

[Edit Button] → onclick="abrirModalEditar(this)" + data-bs-toggle="modal"
                (carries data-* attributes with entity values)
    ↓
Modal opens with pre-filled form (id input enabled)
    ↓
User modifies form → submit intercepted via e.preventDefault()
    ↓
fetch() sends PUT /api/<entity>/{id} with JSON body
    ↓
API returns { success, mensaje, entity }
    ↓
JS updates existing <tr> in-place via actualizarFila<Entity>()
    ↓
Modal closes, success alert shown via mostrarAlerta()

[Delete Button] → onclick="eliminar<Entity>(this)"
    ↓
confirm() dialog
    ↓
fetch() sends DELETE /api/<entity>/{id}
    ↓
JS removes <tr> from DOM, checks if table is empty
    ↓
Success alert shown via mostrarAlerta()
```

### JavaScript Functions in Management Pages

Each management template includes these inline JS functions:

| Function | Purpose |
|----------|---------|
| `abrirModalNueva()` | Resets form fields, sets title to "Nueva..." |
| `abrirModalEditar(btn)` | Reads `data-*` attributes, populates form, sets title to "Editar..." |
| `crearFila<Entity>(data)` | Creates a new `<tr>` element from API response data |
| `actualizarFila<Entity>(fila, data)` | Updates an existing `<tr>` element's cells and data attributes |
| `eliminar<Entity>(btn)` | Sends DELETE via fetch, removes row from DOM |
| `mostrarAlerta(mensaje, tipo)` | Shows a Bootstrap alert (auto-dismisses after 5 seconds) |
| `eliminarFilaVacia()` | Removes the "no records" placeholder row |
| `verificarTablaVacia()` | Adds the "no records" row if table body is empty |
| `escapeHtml(text)` | Escapes HTML entities for safe text insertion |
| `escapeAttr(text)` | Escapes attribute values for safe attribute insertion |

### DOM Structure Conventions

Table rows use these conventions for JavaScript targeting:
- Row IDs: `fila-{entityId}` (e.g. `fila-3`, `fila-550e8400-...`)
- Cell classes: `col-id`, `col-cve`, `col-name`, `col-estado` (divisions); `col-imagen`, `col-nombre`, `col-modalidad`, `col-division` (offerings)
- Empty table placeholder: `id="filaVacia"`
- Alert container: `id="alertContainer"` (above the table)

### Flash Messages (Legacy)

Server-side flash messages are still rendered on initial page load (for backward compatibility) via Thymeleaf:

```html
<div th:if="${mensaje}" class="alert alert-success ...">
<div th:if="${error}" class="alert alert-danger ...">
```

After the initial load, all messages are displayed dynamically via the `mostrarAlerta()` JavaScript function.

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

5. **Standalone form pages:** `NuevaOferta.html` and `NuevaDivision.html` are standalone form pages that duplicate the modal functionality. The management pages now use modals for create/edit, making these pages partially redundant (though they still have working routes). These pages still use the legacy MVC PRG POST endpoints.

6. **PascalCase fields:** `PerfilEgreso` uses PascalCase public fields (`Id`, `Description`, etc.) which deviates from Java naming conventions.

7. **In-memory user storage:** User accounts are stored in-memory with hardcoded credentials (not persisted in database). For production, implement database-backed user storage with proper password management.

8. **Legacy MVC POST routes:** The original `POST /divisiones/guardar`, `POST /ofertas/guardar`, etc. still exist in `DivisionController` and `OfertaController` for backward compatibility with the standalone form pages. The management pages no longer use them (they use the REST API instead).

9. **Admisiones page placeholder:** The `/alumno/admisiones` page is currently empty with only a placeholder message.

---

## 12. Adding a New Entity (Step-by-Step Guide)

To add a new CRUD entity following the existing patterns:

1. **Model:** Create a JPA entity class in `Models/` with `@Data`, `@Entity`, `@Table`.
2. **Repository:** Create an interface in `Repositories/` extending `JpaRepository<YourEntity, PKType>`.
3. **Service:** Create a service class in `Services/` with `obtenerTodas()`, `obtenerPorId()`, `crear()`, `actualizar()`, `eliminar()` methods.
4. **MVC Controller:** Create a `@Controller` in `Controllers/` with:
   - `GET /consola/gestion_<plural>` — management page (pass entity list + any related lists to model)
5. **API Controller:** Create a `@RestController` in `Controllers/` with:
   - `GET /api/<plural>` — list all (returns JSON array)
   - `POST /api/<plural>` — create (accepts JSON body, returns `{ success, mensaje, entity }`)
   - `PUT /api/<plural>/{id}` — update (accepts JSON body, returns `{ success, mensaje, entity }`)
   - `DELETE /api/<plural>/{id}` — delete (returns `{ success, mensaje }`)
   - Include a `toMap()` helper method to serialize entities without circular reference issues.
6. **Template:** Create a Thymeleaf template in `templates/` with:
   - Include navbar fragment: `<nav th:replace="Fragments/fragment1 :: menu"></nav>`
   - Add security namespace if needed: `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`
   - Data table with `th:each` (initial server-side render)
   - Table rows with `id="fila-{entityId}"` and CSS classes on cells for JS targeting
   - Bootstrap modal with form (no `action` attribute — submit handled by JS)
   - JavaScript functions: `abrirModalNueva()`, `abrirModalEditar(btn)`, `crearFila<Entity>()`, `actualizarFila<Entity>()`, `eliminar<Entity>()`
   - `mostrarAlerta()`, `eliminarFilaVacia()`, `verificarTablaVacia()`, `escapeHtml()`, `escapeAttr()`
   - Form submit listener with `e.preventDefault()` + `fetch()` to POST/PUT JSON to the API
   - Delete buttons with `onclick="eliminar<Entity>(this)"` that send DELETE via `fetch()`
   - Alert container `<div id="alertContainer"></div>` for dynamic messages
7. **Navbar:** Add a link to the new management page in `Fragments/fragment1.html` with appropriate `sec:authorize` expression.
8. **Security:** Update `WebSecurityConfig.java` to add authorization rules for the new routes.

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

---

## 14. Security Implementation Summary

**Added:** 2026-03-11

This section summarizes the security implementation for quick reference.

### Users & Credentials

| Username | Password | Roles | Access |
|----------|----------|-------|--------|
| `user` | `1234` | USER | Basic student portal access |
| `coordinador` | `coord` | USER, COORDINADOR | Division management + student portal |
| `admin` | `admin` | USER, ADMIN | Full system access |

### Access Control Summary

- **Public** (no login): Home page (`/`)
- **Authenticated** (any logged-in user): `/ofertas`, `/alumno/*`
- **COORDINADOR or ADMIN**: `/consola/gestion_divisiones`, `/api/divisiones/**`
- **ADMIN only**: `/consola/gestion_ofertas`, `/api/ofertas/**`

### New Files Created

1. `src/main/java/com/scrip/practice/security/WebSecurityConfig.java` — Security configuration
2. `src/main/java/com/scrip/practice/Controllers/AlumnoController.java` — Student portal controller
3. `src/main/resources/templates/Admisiones.html` — Admissions page (placeholder)
4. `src/main/resources/templates/Valores.html` — Institutional values page
5. `src/main/resources/templates/MapaSitio.html` — Site map page
6. `src/main/resources/templates/error/403.html` — Custom 403 Forbidden error page

### Modified Files

1. `build.gradle` — Added `thymeleaf-extras-springsecurity6` dependency
2. `src/main/resources/templates/Fragments/fragment1.html` — Added role-based menu items, user info display, logout button, login button
3. All existing templates — Added security namespace for pages that need role-based rendering

### Key Features

- **Form-based login** with Spring Security default login page
- **BCrypt password encryption**
- **Role-based UI elements** using Thymeleaf `sec:authorize` expressions
- **Role badges** displayed in navbar (Admin/Coordinador/Usuario)
- **Logout button** with POST form to `/logout`
- **Custom 403 error page** with navigation and user role display
- **Student portal dropdown menu** with three pages (Admisiones, Valores, Mapa del Sitio)
