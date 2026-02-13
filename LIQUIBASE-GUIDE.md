# Migración de Flyway a Liquibase - Guía de Uso

## Resumen de cambios

Se ha migrado el proyecto de **Flyway** a **Liquibase** con generación automática de changelogs desde entidades JPA.

## Estructura de archivos

```
src/main/resources/
└── db/
    └── changelog/
        ├── db.changelog-master.yaml          # Changelog maestro (incluye todos los demás)
        └── changes/
            └── 001-initial-schema.yaml       # Changelog inicial con tablas y datos
```

## Configuración aplicada

### 1. build.gradle
- ✅ Removidas dependencias de Flyway
- ✅ Agregada `liquibase-core` (Spring Boot maneja la versión)
- ✅ Agregada extensión `liquibase-hibernate6:4.30.0` para detectar cambios en entidades
- ✅ Creadas tareas Gradle personalizadas

### 2. application.properties
- ✅ Removida configuración de Flyway
- ✅ Agregada configuración de Liquibase
- ✅ Logging DEBUG activado

### 3. Base de datos
- ✅ Tabla `flyway_schema_history` ya no existe
- ✅ Tabla `databasechangelog` creada por Liquibase
- ✅ Tablas `division` y `oferta_educativa` creadas
- ✅ 19 registros seed insertados en `oferta_educativa`

---

## Flujo de trabajo diario

### 1. Modificar una entidad JPA

Ejemplo: Agregar un campo a `Division.java`

```java
@Data
@Entity
@Table(name = "division")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    
    @NotEmpty
    private String cve;
    
    @NotEmpty
    private String name;
    
    private boolean active;
    
    // ⬇️ NUEVO CAMPO
    private String description;  // <-- Agregaste esto
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "division_id")
    private List<OfertaEducativa> OfertasEducativas;
}
```

### 2. Generar changelog automáticamente

```bash
./gradlew diffChangeLog
```

**Esto compara tus entidades JPA contra la BD actual y genera un nuevo changelog YAML con las diferencias.**

### 3. Revisar el changelog generado

Liquibase creará un nuevo archivo en `src/main/resources/db/changelog/changes/` con un nombre como:

```
002-add-description-to-division.yaml
```

**IMPORTANTE:** Revisa este archivo para asegurarte de que los cambios sean correctos. A veces necesitas ajustar:
- Valores por defecto
- Nombres de constraints
- Orden de ejecución

### 4. Incluir el nuevo changelog en el maestro

Edita `db.changelog-master.yaml` y agrega la nueva línea:

```yaml
databaseChangeLog:
  - include:
      file: changes/001-initial-schema.yaml
      relativeToChangelogFile: true
  - include:
      file: changes/002-add-description-to-division.yaml  # <-- NUEVO
      relativeToChangelogFile: true
```

### 5. Aplicar los cambios a la BD

```bash
./gradlew update
```

**Esto ejecuta todos los changelogs pendientes contra tu base de datos.**

### 6. Reiniciar la aplicación

Al reiniciar Spring Boot, Liquibase detectará que los changelogs ya están aplicados y no los volverá a ejecutar.

---

## Comandos útiles

### Ver estado de migraciones
```bash
./gradlew liquibaseStatus
```

Muestra qué changelogs están aplicados y cuáles están pendientes.

### Aplicar migraciones pendientes
```bash
./gradlew update
```

### Generar changelog de diferencias
```bash
./gradlew diffChangeLog
```

Detecta cambios en entidades JPA y genera un changelog.

### Rollback (requiere configuración adicional)
```bash
./gradlew rollbackCount -ProllbackCount=1
```

---

## Comportamiento de Spring Boot

Cuando inicias la aplicación con Spring Boot:

1. **Spring Boot detecta** `liquibase-core` en el classpath
2. **Lee la configuración** de `application.properties`
3. **Busca el changelog maestro** en `classpath:db/changelog/db.changelog-master.yaml`
4. **Verifica** qué changelogs ya están aplicados (tabla `databasechangelog`)
5. **Aplica automáticamente** los changelogs pendientes al iniciar
6. **Registra** cada changeset ejecutado en la tabla `databasechangelog`

Por eso **NO necesitas ejecutar manualmente** `./gradlew update` cada vez. Spring Boot lo hace automáticamente al arrancar.

---

## Ventajas del nuevo enfoque

### ✅ Generación automática
Ya no escribes SQL manualmente. Liquibase detecta los cambios en tus entidades `@Entity` y genera el SQL por ti.

### ✅ Historial versionado
Cada cambio queda registrado con:
- ID único
- Autor
- Fecha/hora de ejecución
- Checksum para detectar modificaciones

### ✅ Rollback seguro
Puedes revertir cambios de forma controlada (requiere configurar rollback changesets).

### ✅ Múltiples BD
El mismo changelog funciona en PostgreSQL, MySQL, Oracle, etc. Liquibase genera el SQL apropiado.

### ✅ Validación
Liquibase valida que el changelog no haya sido modificado después de aplicarse (mediante checksums).

---

## Ejemplo completo de workflow

```bash
# 1. Modificas Division.java (agregas campo 'description')
# 2. Generas el changelog
./gradlew diffChangeLog

# 3. Ves el changelog generado
cat src/main/resources/db/changelog/changes/002-*.yaml

# 4. Agregas el include al maestro (editas db.changelog-master.yaml)

# 5. Aplicas los cambios
./gradlew update

# 6. Verificas el estado
./gradlew liquibaseStatus

# 7. Reinicias la app (Spring Boot ve que ya está aplicado)
./gradlew bootRun
```

---

## Solución de problemas

### La app no aplica las migraciones al arrancar

**Causa:** Spring Boot ya detectó que los changelogs están aplicados.

**Solución:** Normal. Si acabas de ejecutar `./gradlew update`, Spring Boot no volverá a aplicarlos.

### Error "Changeset already exists"

**Causa:** Intentas agregar un changeset con un ID que ya existe.

**Solución:** Usa IDs únicos y secuenciales (001, 002, 003...).

### Liquibase dice "No changes detected"

**Causa:** No hay diferencias entre tus entidades JPA y la BD actual.

**Solución:** Normal. Significa que tu BD está sincronizada con tus entidades.

### Error de checksum

**Causa:** Modificaste un changelog que ya fue aplicado.

**Solución:** **NUNCA modifiques changelogs ya aplicados.** Crea uno nuevo con los cambios adicionales.

---

## Buenas prácticas

1. **Nunca modifiques changelogs aplicados** - Siempre crea uno nuevo
2. **Revisa los changelogs generados** - A veces necesitan ajustes manuales
3. **Usa IDs descriptivos** - Ej: `001-create-initial-schema`, `002-add-user-email`
4. **Commitea los changelogs** - Son parte del código fuente
5. **Prueba rollbacks** - Configura tags para poder revertir cambios
6. **Un changeset = un cambio lógico** - No mezcles múltiples cambios no relacionados

---

## Referencias

- [Documentación Liquibase](https://docs.liquibase.com)
- [Liquibase + Hibernate](https://github.com/liquibase/liquibase-hibernate)
- [Spring Boot + Liquibase](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
