package com.jsalmar.aad;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Esto es un gestor de logs para la práctica Act_1_5.
 * Esto es para tener una guia tm para mi:
 * Intentar poner la mayoria de las cosas en ingles
 * Funcionalidades:
 * Añadir evento al fichero app.log con marca temporal [Año-Mes-Dia Hora:minuto:segundos]
 * Filtrar eventos por fecha (Años-Mes-Dias)
 * Configurar codificación (UTF-8 por defecto y si puedo tambien una opción ISO-8859-1)
 * Hay un requerimiento:
 * JDK 8+.
 */

/**
 * Webgrafia
 * https://www.geeksforgeeks.org/java/file-getparentfile-method-in-java-with-examples/
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api//org/springframework/web/filter/CharacterEncodingFilter.html
 * Muchas cosas buscadas la daba la ia del copilot pero no las usaba , las interpretaba como un ejemplo
 */

@Slf4j
public class Logs {

    public static class LogManager {

        private static final String DEFAULT_LOG_FILENAME = "app.log";
        private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final DateTimeFormatter DATE_ONLY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        private File logFile;
        private String encoding = "UTF-8"; // por defecto UTF-8

        public LogManager() {
            this(DEFAULT_LOG_FILENAME);
        }

        public LogManager(String filename) {
            this.logFile = new File(filename);
            ensureLogFile();
        }


        //Aplicación de consola simple con menú.


        public static void main(String[] args) {
            LogManager manager = new LogManager();
            Scanner scanner = new Scanner(System.in, "UTF-8");

            log.info("     Gestor de Logs     ");
            boolean running = true;

            while (running) {

                log.info("Codificación actual: " + manager.encoding);
                log.info("1) Añadir evento");
                log.info("2) Filtrar eventos por fecha (Año-Mes-Dia)");
                log.info("3) Cambiar codificación (UTF-8 / ISO-8859-1)");
                log.info("4) Mostrar todo el log");
                log.info("5) Salir");
                log.info("Elige una opción: ");

                String opt = scanner.nextLine().trim();
                try {
                    switch (opt) {
                        case "1":
                            log.info("Escriba el mensaje: ");
                            String msg = scanner.nextLine();
                            manager.addEvent(msg);
                            log.info("Evento añadido...");
                            break;
                        case "2":
                            log.info("Fecha (Año-Mes-Dia): ");
                            String date = scanner.nextLine().trim(); // trim() para quitar espacios al inicio y al final
                            manager.filterByDate(date);
                            break;
                        case "3":
                            log.info("Nueva codificación (UTF-8 o ISO-8859-1): ");
                            String enc = scanner.nextLine().trim();
                            if (!enc.equalsIgnoreCase("UTF-8") && !enc.equalsIgnoreCase("ISO-8859-1")) {
                                log.info("Codificacion no soportada...  seguiremos con el: " + manager.encoding); // .encoding se refiere a la configuración de codificación de caracteres que utiliza la JVM (Java Virtual Machine) para interpretar y manejar texto , lo he sacado de internet esto...
                            } else {
                                manager.setEncoding(enc);
                                log.info("Codificacion cambiada a " + manager.encoding); //Añado que el .encoding es para el tema del UTF-8 y el ISO-8859-1 por eso lo pongo
                            }
                            break;
                        case "4":
                            manager.showAll();
                            break;
                        case "5":
                            running = false;
                            break;
                        default:
                            log.info("Opción no válida... ");
                    }
                } catch (DateTimeParseException dtp) {
                    log.info("El formato que usas no sirve. Usa este para que funcione Año-Mes-Dia.");
                } catch (IOException ioe) {
                    log.info("Error de E/S (Recurso no existe): " + ioe.getMessage());
                } catch (Exception ex) {
                    log.info("UPS que ha pasado , error: " + ex.getMessage());
                }
            }

            log.info("Saliendo la de aplicacion , chao...");
            scanner.close();
        }


        // Asegura la existencia del fichero de log (crea si no existe).

        private void ensureLogFile() {
            try {
                File parent = logFile.getAbsoluteFile().getParentFile(); // Esta función devuelve el archivo primario del objeto de archivo dado. La función devuelve un objeto File que contiene el archivo Parent del objeto de archivo dado. Si la ruta abstracta no contiene ningún archivo primario, se devuelve un valor nulo.
                if (parent != null && !parent.exists()) { // Si el archivo primario no existe lo crea
                    if (!parent.mkdirs()) {
                        log.error("No se pudieron crear directorios: " + parent.getAbsolutePath());
                    }
                }
                if (!logFile.exists()) { // Si el archivo existe da error porque ya existe
                    if (!logFile.createNewFile()) {
                        log.error("No se pudo crear el fichero de log: " + logFile.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                log.error("Error al asegurar fichero de log: " + e.getMessage());
            }
        }

        /**
         * Cambia la codificación que se usará para leer o escribir el log.
         * <p>
         * Un encoding nombre de la codificación para el tema del "UTF-8" o "ISO-8859-1"
         */

        public void setEncoding(String encoding) {
            if (encoding == null) return;
            this.encoding = encoding;
        }

        /**
         * Añade un evento al fichero con la marca temporal actual.
         * <p>
         * Mensaje de texto del evento
         * Tiene que lanzar un IOException en caso de que haya error de E/S
         *
         */

        public void addEvent(String message) throws IOException {
            if (message == null) message = "";
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
            String line = String.format("[%s] %s", timestamp, message);

            // usando OutputStreamWriter para controlar codificación
            try (FileOutputStream fos = new FileOutputStream(logFile, true);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
                 BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
        }

        /**
         * Muestra por consola todas las líneas del log que correspondan a la fecha indicada.
         * <p>
         * Parametros : dateStr fecha en formato Año-Mes-Dia
         * Lanzar un IOException por si hay error de lectura
         * Y hay tm que lanzar un DateTimeParseException si el formato de fecha no es válido
         */

        public void filterByDate(String dateStr) throws IOException, DateTimeParseException {
            // validar formato de fecha
            DATE_ONLY_FMT.parse(dateStr); // lanza DateTimeParseException si no es válido
            String prefix = "[" + dateStr; // las líneas empiezan con "[" y despues Año-Mes-Dia ...

            try (FileInputStream fis = new FileInputStream(logFile);
                 InputStreamReader isr = new InputStreamReader(fis, encoding);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                boolean any = false;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(prefix)) {
                        log.info(line);
                        any = true;
                    }
                }
                if (!any) {
                    log.info("No se encontraron eventos para la fecha " + dateStr);
                }
            }
        }

        /**
         * Muestra todo el log por consola
         * <p>
         * Que lance un IOException por si hay error de lectura
         */

        public void showAll() throws IOException {
            try (FileInputStream fis = new FileInputStream(logFile);
                 InputStreamReader isr = new InputStreamReader(fis, encoding);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    log.info(line);
                }
            }
        }
    }

}
