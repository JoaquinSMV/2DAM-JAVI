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
    public void run(String... args) {
        File f = new File("ejemplo.txt");

        if (f.exists()) {
            System.out.println("El fichero existe.");
            if (f.isFile()) {
                System.out.println("Es un fichero.");
                System.out.println("Tamaño: " + f.length() + " bytes");
            } else if (f.isDirectory()) {
                System.out.println("Es un directorio.");
            }
        } else {
            System.out.println("El fichero no existe.");
        }
    }
}

