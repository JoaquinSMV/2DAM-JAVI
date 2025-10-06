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
        try {
            BufferedImage img = ImageIO.read(new File("C:\\Users\\joaqu\\Documents\\Workspace\\AADD1\\src\\main\\resources\\Perro.jpg"));

            // Escalar la imagen para que quepa en consola
            int newWidth = 100; // ancho en caracteres
            int newHeight = (img.getHeight() * newWidth) / img.getWidth();
            BufferedImage scaled = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_RGB);
            scaled.getGraphics().drawImage(img, 0, 0, newWidth, newHeight, null);

            // Gradiente de caracteres de más oscuro a más claro
            String gradient = "@#8&xo;:,. ";

            for (int y = 0; y < newHeight; y += 2) { // saltamos filas para corregir proporción
                for (int x = 0; x < newWidth; x++) {
                    Color c = new Color(scaled.getRGB(x, y));
                    int gris = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

                    int index = (gris * (gradient.length() - 1)) / 255;
                    System.out.print(gradient.charAt(index));
                }


                System.out.println();
            }

        } catch (IOException e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }
    }
}
