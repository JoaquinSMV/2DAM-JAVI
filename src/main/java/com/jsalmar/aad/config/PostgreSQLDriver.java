package com.jsalmar.aad.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PostgreSQLDriver {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    private Connection connection;

    /**
     * Se ejecuta automáticamente al iniciar la aplicación
     * Establece la conexión y ejecuta los scripts SQL
     */
    @PostConstruct
    public void init() {
        try {
            log.info("Iniciando conexión a PostgreSQL...");
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, username, password);
            log.info("Conexión a PostgreSQL establecida correctamente");

            // Ejecutar scripts SQL en orden
            executeScriptFromFile("sql/01_schema.sql");
            executeScriptFromFile("sql/02_procedures.sql");

            log.info("Scripts SQL ejecutados correctamente");
        } catch (ClassNotFoundException e) {
            log.error("Driver de PostgreSQL no encontrado", e);
        } catch (SQLException e) {
            log.error("Error al conectar con PostgreSQL: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta un archivo SQL desde resources
     *
     * @param filePath ruta del archivo SQL relativa a resources
     */
    private void executeScriptFromFile(String filePath) {
        try {
            log.info("Ejecutando script: {}", filePath);
            ClassPathResource resource = new ClassPathResource(filePath);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String sqlScript = reader.lines().collect(Collectors.joining("\n"));

                // Dividir por punto y coma para ejecutar cada statement
                String[] statements = sqlScript.split(";");

                try (Statement stmt = connection.createStatement()) {
                    for (String sql : statements) {
                        String trimmedSql = sql.trim();
                        if (!trimmedSql.isEmpty() && !trimmedSql.startsWith("--")) {
                            stmt.execute(trimmedSql);
                        }
                    }
                }

                log.info("Script {} ejecutado correctamente", filePath);
            }
        } catch (Exception e) {
            log.error("Error al ejecutar el script {}: {}", filePath, e.getMessage(), e);
        }
    }

    /**
     * Obtiene la conexión a la base de datos
     *
     * @return Connection objeto de conexión
     * @throws SQLException si la conexión no está disponible
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Conexión no disponible");
        }
        if (connection != null) return connection;
        return DriverManager.getConnection(url, username, password);

    }

    /**
     * Cierra la conexión al destruir el componente
     */
    @PreDestroy
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Conexión a PostgreSQL cerrada correctamente");
            }
        } catch (SQLException e) {
            log.error("Error al cerrar la conexión: {}", e.getMessage());
        }
    }

    /**
     * Verifica si la conexión está activa
     *
     * @return true si la conexión está activa, false en caso contrario
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.error("Error al verificar la conexión: {}", e.getMessage());
            return false;
        }
    }

    public void beginTransaction() throws SQLException {
        if (connection != null) throw new IllegalStateException("connection already active");
        connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        if (connection == null) throw new IllegalStateException("No active connection");
        try {
            connection.commit();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Close error: {}", e.getMessage());
            }
            connection = null;
        }
    }

    public void rollback() {
        if (connection == null) return;
        try {
            connection.rollback();
        } catch (SQLException e) {
            log.error("Rollback error: {}", e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Close error: {}", e.getMessage());
            }
        }
    }
}
