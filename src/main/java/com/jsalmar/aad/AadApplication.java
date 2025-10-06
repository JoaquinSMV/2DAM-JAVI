package com.jsalmar.aad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class AadApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        File carpeta = new File("datos");
        if (!carpeta.exists()) {
            carpeta.mkdir();
            System.out.println("Carpeta creada.");
        }

        File fichero = new File(carpeta, "alumnos.txt");
        if (fichero.createNewFile()) {
            System.out.println("Fichero creado en: " + fichero.getAbsolutePath());
        }
    }
}

