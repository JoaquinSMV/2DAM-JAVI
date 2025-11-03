package com.jsalmar.aad;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PostgresqlDriver {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("classpath:sql/*.sql")
    private Resource[] scripts;

    @PostConstruct
    public void init() {
        log.info("Connecting to database...");
        for (Resource script : scripts) {
            executeSql(script);
        }
        log.info("Database connection established.");
    }

    private void executeSql(Resource resource) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new
                     InputStreamReader(resource.getInputStream()))) {
            String sql = reader.lines().collect(Collectors.joining("\n"));
            stmt.execute(sql);
            log.info("Executed script: {}", resource.getFilename());
        } catch (Exception e) {
            log.error("⚠️ Error executing script {}: {}",
                    resource.getFilename(), e.getMessage());
        }
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
