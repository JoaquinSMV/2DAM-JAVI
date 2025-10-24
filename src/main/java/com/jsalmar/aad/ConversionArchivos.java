package com.jsalmar.aad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/*
 * Clase ConversionArchivos
 * ------------------------
 * Esta clase se encarga de leer un archivo CSV con datos de alumnos
 * y convertirlo a otros formatos: JSON o XML.
 *
 * Utiliza la librería Jackson (ObjectMapper / XmlMapper) para serializar
 * los objetos Java (alumnos) en esos formatos.
 *
 * Métodos principales:
 * - menu(): muestra un menú en consola y llama al método de conversión seleccionado.
 * - CSVtoJSON(): convierte el archivo CSV a un archivo JSON.
 * - CSVtoXML(): convierte el archivo CSV a un archivo XML.
 */


@Slf4j
public class ConversionArchivos {

    // Archivo CSV de entrada ubicado en resources
    File csv = new File("src/main/resources/alumnos.csv");
    private Scanner sc = new Scanner(System.in);

    public void menu() {
        try {
            log.info("     CONVERSION DE ARCHIVOS     ");
            log.info("1. Convertir CSV a JSON");
            log.info("2. Convertir CSV a XML");
            log.info("Ingrese una opcion: ");

            int option = sc.nextInt();

            switch (option) {
                case 1:
                    log.info("Se está convirtiendo el archivo CSV a JSON...");
                    CSVtoJSON();
                    break;
                case 2:
                    log.info("Se está convirtiendo el archivo CSV a XML...");
                    CSVtoXML();
                    break;
                default:
                    log.error("Opción no válida, introduzca 1 o 2...");
                    break;
            }

        } catch (Exception e) {
            log.error("Error al ingresar la opción: " + e.getMessage());
        }

        sc.close(); // Cierra el scanner al final
    }

    public void CSVtoJSON() {
        ObjectMapper mapper = new ObjectMapper();
        List<alumnos> ListaAlumnos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {

            String line;
            br.readLine(); // Saltamos la primera línea (cabecera)

            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Separamos los datos por comas

                if (data.length == 3) { // Validamos que haya 3 columnas
                    int id = Integer.parseInt(data[0].trim());
                    String nombre = data[1].trim();
                    double nota = Double.parseDouble(data[2].trim());
                    alumnos alumno = new alumnos(id, nombre, nota);
                    ListaAlumnos.add(alumno);
                }
            }

            // Serializamos la lista de alumnos a JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/alumnos.json"), ListaAlumnos);
            log.info("Archivo CSV convertido a JSON correctamente.");

        } catch (Exception e) {
            log.error("Error al convertir CSV a JSON: " + e.getMessage());
        }
    }

    public void CSVtoXML() {
        XmlMapper mapper = new XmlMapper();
        List<alumnos> ListaAlumnos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {

            String line;
            br.readLine(); // Saltamos la cabecera

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    int id = Integer.parseInt(data[0].trim());
                    String nombre = data[1].trim();
                    double nota = Double.parseDouble(data[2].trim());
                    alumnos alumno = new alumnos(id, nombre, nota);
                    ListaAlumnos.add(alumno);
                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/alumnos.xml"), ListaAlumnos);
            log.info("Archivo CSV convertido a XML correctamente.");

        } catch (Exception e) {
            log.error("Error al convertir CSV a XML: " + e.getMessage());
        }
    }
}
