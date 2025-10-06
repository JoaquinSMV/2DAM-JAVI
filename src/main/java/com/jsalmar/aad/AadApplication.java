package com.jsalmar.aad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class AadApplication {   // 👈 Declaración de clase

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    public class FicheroTexto {
        public static void main(String[] args) throws IOException {
            Path ruta = Paths.get("alumnos.txt");

            // Escribir en el fichero
            Files.writeString(ruta, "ID,Nombre\n1,Ana\n2,Juan", StandardCharsets.UTF_8);

            // Leer del fichero
            String contenido = Files.readString(ruta, StandardCharsets.UTF_8);
            System.out.println("Contenido del fichero:");
            System.out.println(contenido);
        }
    }

}


