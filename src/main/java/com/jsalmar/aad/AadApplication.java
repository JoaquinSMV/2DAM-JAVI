package com.jsalmar.aad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class AadApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream("C:\\Users\\joaqu\\Documents\\Workspace\\AADD1\\src\\main\\resources\\Perro.jpg"))) {

            byte[] buffer = new byte[1024];
            int bytesLeidos;
            int total = 0;

            while ((bytesLeidos = bis.read(buffer)) != -1) {
                total += bytesLeidos;
            }
            System.out.println("Imagen leída con éxito. Total bytes: " + total);
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }
}
