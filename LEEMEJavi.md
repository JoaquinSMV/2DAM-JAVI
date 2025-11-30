## 1. Objetivo y Resumen de la Migración

El objetivo de esta actividad fue el migrar el proyecto de JDBC puro a Spring Data JDBC, eliminando la parte manual de
la conexión y de las transacciones.

El cambio principal fue sustituir las clases nativas de JDBC (Connection, PreparedStatement, ResultSet,
CallableStatement) por el patrón (JdbcTemplate) y el control declarativo de Spring:

* **Repositorios:** Ahora dependen de JdbcTemplate para todas las operaciones CRUD.
* **Conexión:** Se usa el DataSource autoconfigurado por Spring Boot, gestionado desde (application.yml).
* **Transacciones:** Se eliminaron los ( commit() ) y ( rollback() ) manuales.

---

## 2. Ventajas y Gestión Automática de Recursos

### **Ventajas Clave de JdbcTemplate**

| Aspecto                | JDBC Tradicional                                                       | Spring JdbcTemplate                                                                                                                     |
|:-----------------------|:-----------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------|
| **Recursos (close())** | Gestión manual de Connection, Statement, ResultSet (propenso a fugas). | **Automático.** JdbcTemplate maneja el *pool* de conexiones y el cierre de recursos internamente                                        |
| **Excepciones**        | Genéricas (SQLException).                                              | **Excepciones Uniformes.** Mapea errores específicos de PostgreSQL a la jerarquía DataAccessException de Spring (más fácil de manejar). |
| **Mapeo de Datos**     | Manual con while(rs.next()).                                           | **Sencillo y Limpio.** Se usa RowMapper o lambdas para mapear automáticamente el resultado a objetos Java.                              |

---

## 3. Transacciones y Procedimientos

### **Transacciones Declarativas**

Se eliminó el manejo explícito de commit/rollback y se adoptó el modelo declarativo de Spring:

* La anotación **@Transactional** se aplica en la capa de servicio (ManagementService), en métodos como createS o
  enrollstudent.


* Spring AOP envuelve estos métodos:

    * Si el método termina sin errores, se realiza el commit automáticamente.
    * Si ocurre una excepción (RuntimeException), se realiza el **rollback automático**, asegurando que las
      operaciones (como la creación de un alumno y un módulo) sean atómicas e indivisibles.

### **Procedimientos Almacenados**

Para la invocación de funciones y procedimientos, se reemplazó el CallableStatement manual por **SimpleJdbcCall** en el
EnrollMentRepository.

Esta clase simplifica la configuración de parámetros de entrada y salida, permitiendo la ejecución limpia de lógica de
negocio en la base de datos (e.g., count_enrollments).

---