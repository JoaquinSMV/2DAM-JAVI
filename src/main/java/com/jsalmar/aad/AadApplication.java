package com.jsalmar.aad;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
public class AadApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Path ruta = Paths.get("ejemploNIO.txt");

        if (!Files.exists(ruta)) {
            Files.createFile(ruta);
            log.info("Fichero creado con NIO.2");
        }

        // Escribir texto en el fichero
        Files.write(ruta, "Hola mundo desde NIO.2".getBytes());

        // Leer todo el contenido
        String contenido = Files.readString(ruta);
        log.info("Contenido: " + contenido);
    }
}
    }
}
