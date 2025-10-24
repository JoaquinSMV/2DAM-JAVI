package com.jsalmar.aad;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Clase AadApplication
 * --------------------
 * Clase principal de la aplicación Spring Boot.
 *
 * Implementa CommandLineRunner para ejecutar código
 * inmediatamente después de iniciar la aplicación.
 *
 * En este caso, crea un objeto ConversionArchivos y
 * llama a su método menu() para iniciar la conversión.
 */

@SpringBootApplication
@Slf4j
public class AadApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ConversionArchivos conversionArchivos = new ConversionArchivos();
        log.info("Se esta ejecutando Conversion de Archivos :)");
        conversionArchivos.menu();
    }
}
