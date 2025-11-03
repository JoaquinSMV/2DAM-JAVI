package com.jsalmar.aad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class AadApplication implements CommandLineRunner {
    private final PostgresqlDriver postgresqlDriver;

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Testing JDBC connection...");
        try (Connection conn = postgresqlDriver.getConnection()) {
            log.info("Connection successful: {}",
                    conn.getMetaData().getURL());
            log.info("Database: {}",
                    conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}