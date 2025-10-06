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
            BufferedImage img = ImageIO.read(new File("C:\\Users\\joaqu\\Documents\\Workspace\\AADD1\\src\\main\\resources\\Gonza.jpg"));

            // Escalar la imagen para que quepa en consola
            int newWidth = 80; // caracteres de ancho
            int newHeight = (img.getHeight() * newWidth) / img.getWidth();
            BufferedImage scaled = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_RGB);
            scaled.getGraphics().drawImage(img, 0, 0, newWidth, newHeight, null);



            for (int y = 0; y < newHeight - 1; y += 2) { // procesamos de dos en dos
                for (int x = 0; x < newWidth; x++) {
                    // Color del pixel de arriba
                    Color top = new Color(scaled.getRGB(x, y));
                    // Color del pixel de abajo
                    Color bottom = new Color(scaled.getRGB(x, y + 1));

                    // ANSI: color de texto = top, fondo = bottom
                    System.out.print(
                            "\u001B[38;2;" + top.getRed() + ";" + top.getGreen() + ";" + top.getBlue() +
                                    "m" +
                                    "\u001B[48;2;" + bottom.getRed() + ";" + bottom.getGreen() + ";" +
                                    bottom.getBlue() + "m" +
                                    "▀" // bloque superior coloreado
                    );
                }
                System.out.print("\u001B[0m\n"); // reset al final de la línea
            }

        } catch (IOException e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }
    }
}
