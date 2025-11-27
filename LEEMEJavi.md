# Proyecto AAD: Gestión Académica (JDBC Puro + PostgreSQL)

## Resumen y Objetivo

Este proyecto implementa una solución completa de **Acceso a Datos** (AAD) utilizando **Java Spring Boot** y **JDBC puro
** para gestionar estudiantes y módulos en una base de datos **PostgreSQL**.

El enfoque principal es demostrar el manejo avanzado y de bajo nivel de la persistencia mediante: JDBC, Transacciones
explícitas, *Scripts* SQL y Procedimientos Almacenados.

-----

## Entorno de Despliegue (Punto b: PostgreSQL en Docker)

Para asegurar un entorno de base de datos **independiente y reproducible**, utilizamos **Docker Compose** para gestionar
el servicio de PostgreSQL, cumpliendo así con el requisito **b** (2 puntos).

### 1\. Instrucciones de Inicio

Para levantar el contenedor de la base de datos, navega a la raíz del proyecto y ejecuta el siguiente comando:

```bash
docker-compose up -d
```

### 2\. Estructura de Docker Compose (Evidencia)

**Archivo `docker-compose.yml`:**

```yaml
version: '3.8'
services:
  postgres-db:
    image: postgres:15-alpine # Imagen oficial de PostgreSQL
    container_name: postgres_aad
    ports:
      - "5432:5432" # Puerto mapeado al host
    environment:
      # Las credenciales se leen desde la configuración de Spring Boot
      POSTGRES_USER: tu_usuario
      POSTGRES_PASSWORD: tu_password
      POSTGRES_DB: tu_base_de_datos
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

-----

## Análisis Teórico: Ventajas/Inconvenientes de JDBC (Punto a)

Este análisis justifica la elección del **Conector JDBC puro** para la capa de persistencia (sin ORMs), cumpliendo con
el requisito **a** (2 puntos).

| Aspecto                    | **Ventajas de JDBC Puro**                                                                                                                              | **Inconvenientes de JDBC Puro**                                                                                                                    |
|:---------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Control / Optimización** | **Control Total:** Permite optimizar al máximo las *queries* SQL y acceder a funcionalidades específicas de PostgreSQL (como los `CallableStatement`). | **Código Verboso (Boilerplate):** Requiere mucho código repetitivo para la gestión de recursos JDBC y el mapeo manual de datos.                    |
| **Rendimiento**            | **Alto Rendimiento:** Ausencia de capa de abstracción de ORM, lo que resulta en una ejecución muy eficiente de las sentencias SQL.                     | **Baja Productividad:** El desarrollo de métodos CRUD para cada entidad es más lento, ya que cada operación debe ser codificada a mano.            |
| **Características**        | Permite el uso directo de funciones avanzadas de la base de datos, como la invocación de **procedimientos almacenados** (Punto k).                     | **Mayor Riesgo de Errores:** La gestión manual de `ResultSet` y la composición de sentencias son más propensas a errores de programación (*bugs*). |

-----

## ️ Características Avanzadas de JDBC

La implementación de los repositorios y el servicio de gestión demuestran el cumplimiento de los siguientes requisitos
avanzados:

### Transacciones (Punto j)

El método `ManagementService.enrollstudent()` gestiona la matriculación como una **transacción atómica** explícita,
asegurando la integridad de los datos:

* Se utiliza `postgreSQLDriver.commit()` para confirmar la operación al finalizar con éxito.
* Se utiliza `postgreSQLDriver.rollback()` en el bloque *catch* para deshacer los cambios si ocurre cualquier fallo.

### Cierre de Recursos (Punto i)

Todos los repositorios (`StudentJdbcRepository`, `ModuleRepository`, etc.) utilizan el patrón **`try-with-resources`**
para manejar `Connection`, `PreparedStatement` y `ResultSet`. Esto garantiza el cierre automático y seguro de todos los
recursos JDBC tras su uso, previniendo fugas de memoria y bloqueos de conexión.

### Procedimientos Almacenados (Punto k)

La clase `EnrollMentRepository` invoca la función `count_enrollments` de PostgreSQL utilizando un **`CallableStatement`
**, demostrando la capacidad de ejecutar lógica de negocio directamente en el servidor de la base de datos.

### Java

```
Ejemplo de invocación en EnrollMentRepository.java ;

cs.prepareCall("{ ? = call count_enrollments(?) }");
```