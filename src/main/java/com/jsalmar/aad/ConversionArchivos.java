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

@Slf4j
public class ConversionArchivos {


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
                    log.info("Se esta convertiendo el archivo CSV a JSON...");
                    CSVtoJSON();
                    break;
                case 2:
                    log.info("Se esta convertiendo el archivo CSV a XML...");
                    CSVtoXML();
                    break;
                default:
                    log.error("Opcion no valida , introduzca o 1 o 2 ...");
                    break;
            }

        } catch (Exception e) {
            log.error("Error al ingresar la opcion: " + e.getMessage());
        }

        sc.close();

    }

    public void CSVtoJSON() {

        ObjectMapper mapper = new ObjectMapper();
        List<alumnos> ListaAlumnos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); //Separamos los datos por comas
                if (data.length == 3) //Si hay 3 datos
                {
                    int id = Integer.parseInt(data[0].trim()); //Convertimos a entero
                    String nombre = data[1].trim(); // Sin espacios
                    double nota = Double.parseDouble(data[2].trim()); // Convertimos a double
                    alumnos alumno = new alumnos(id, nombre, nota); // Creamos el objeto
                    ListaAlumnos.add(alumno); // Agregamos el alumno al array

                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/alumnos.json"), ListaAlumnos);
            log.info("Archivo CSV convertido a JSON");


        } catch (Exception e) {
            log.error("Error a la hora de converger CSV...JSON " + e.getMessage());
        }

    }

    public void CSVtoXML() {

        XmlMapper mapper = new XmlMapper();
        List<alumnos> ListaAlumnos = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    int id = Integer.parseInt(data[0].trim());
                    String nombre = data[1].trim();
                    double nota = Double.parseDouble(data[2].trim());
                    alumnos alumno = new alumnos(id, nombre, nota);
                    ListaAlumnos.add(alumno);
                }

                mapper.writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/alumnos.xml"), ListaAlumnos);
                log.info("Archivo CSV convertido a XML");

            }

        } catch (Exception e) {
            log.error("Error a la hora de converger CSV...XML " + e.getMessage());
        }


    }

}
