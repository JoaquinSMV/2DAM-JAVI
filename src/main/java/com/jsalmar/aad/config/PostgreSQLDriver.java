package com.jsalmar.aad.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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

    @PostConstruct
    public void init() {
        try {
            log.info("Iniciando conexión a PostgreSQL...");
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, username, password);
            log.info("Conexión a PostgreSQL establecida correctamente");

            // Listar archivos SQL
            listSqlFiles();

            // Ejecutar scripts en orden
            executeScriptFromFile("sql/01_schema.sql");
            executeScriptFromFile("sql/02_procedures.sql");

            log.info("Todos los scripts SQL ejecutados correctamente");
        } catch (ClassNotFoundException e) {
            log.error("Driver de PostgreSQL no encontrado", e);
        } catch (SQLException e) {
            log.error("Error al conectar con PostgreSQL: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado en init: {}", e.getMessage(), e);
        }
    }

    private void listSqlFiles() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:sql/*.sql");
            log.info("Archivos SQL encontrados en classpath:");
            for (Resource resource : resources) {
                log.info(" - {}", resource.getFilename());
            }
        } catch (Exception e) {
            log.warn("No se pudieron listar archivos SQL: {}", e.getMessage());
        }
    }

    public void executeScriptFromFile(String filePath) {
        try {
            log.info("Buscando script: {}", filePath);
            ClassPathResource resource = new ClassPathResource(filePath);

            if (!resource.exists()) {
                log.error("Archivo no encontrado: {}", filePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                // Leer todo el contenido del archivo
                String sqlScript = reader.lines().collect(Collectors.joining("\n"));
                log.info("Ejecutando script de {} caracteres", sqlScript.length());

                // Ejecutar usando ScriptUtils (evita problemas de $$)
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sqlScript);
                }

                log.info("Script {} ejecutado correctamente", filePath);
            }
        } catch (Exception e) {
            log.error("Error al ejecutar el script {}: {}", filePath, e.getMessage(), e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

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

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.error("Error al verificar la conexión: {}", e.getMessage());
            return false;
        }
    }

    // ----------------------------
    // Manejo de transacciones...
    // ----------------------------
    public void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn.getAutoCommit()) {
            conn.setAutoCommit(false);
        }
    }

    public void commit() throws SQLException {
        Connection conn = getConnection(); // usar la conexión interna
        if (!conn.getAutoCommit()) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    public void rollback() {
        try {
            Connection conn = getConnection(); // usar la conexión interna
            if (!conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Error en rollback: {}", e.getMessage());
        }
    }


}
