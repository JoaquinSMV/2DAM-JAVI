package com.jsalmar.aad;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Gestor simple de logs para la práctica Act_1_5.
 * <p>
 * Funcionalidades:
 * - Añadir evento al fichero app.log con marca temporal [YYYY-MM-DD HH:mm:ss]
 * - Filtrar eventos por fecha (YYYY-MM-DD)
 * - Configurar codificación (UTF-8 por defecto, opción ISO-8859-1)
 * <p>
 * Requiere JDK 8+.
 */

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

        /**
         * Aplicación de consola simple con menú.
         */
        public static void main(String[] args) {
            LogManager manager = new LogManager();
            Scanner scanner = new Scanner(System.in, "UTF-8");

            System.out.println("=== Gestor de Logs (Act_1_5) ===");
            boolean running = true;

            while (running) {
                System.out.println();
                System.out.println("Codificación actual: " + manager.encoding);
                System.out.println("1) Añadir evento");
                System.out.println("2) Filtrar eventos por fecha (Año-Mes-Dia)");
                System.out.println("3) Cambiar codificación (UTF-8 / ISO-8859-1)");
                System.out.println("4) Mostrar todo el log");
                System.out.println("5) Salir");
                System.out.print("Elige una opción: ");

                String opt = scanner.nextLine().trim();
                try {
                    switch (opt) {
                        case "1":
                            System.out.print("Mensaje: ");
                            String msg = scanner.nextLine();
                            manager.addEvent(msg);
                            System.out.println("Evento añadido...");
                            break;
                        case "2":
                            System.out.print("Fecha (Año-Mes-Dia): ");
                            String date = scanner.nextLine().trim();
                            manager.filterByDate(date);
                            break;
                        case "3":
                            System.out.print("Nueva codificación (UTF-8 o ISO-8859-1): ");
                            String enc = scanner.nextLine().trim();
                            if (!enc.equalsIgnoreCase("UTF-8") && !enc.equalsIgnoreCase("ISO-8859-1")) {
                                System.out.println("Codificación no soportada... Manteniendo: " + manager.encoding);
                            } else {
                                manager.setEncoding(enc);
                                System.out.println("Codificación cambiada a " + manager.encoding);
                            }
                            break;
                        case "4":
                            manager.showAll();
                            break;
                        case "5":
                            running = false;
                            break;
                        default:
                            System.out.println("Opción no válida.");
                    }
                } catch (DateTimeParseException dtp) {
                    System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
                } catch (IOException ioe) {
                    System.out.println("Error de E/S: " + ioe.getMessage());
                } catch (Exception ex) {
                    System.out.println("Error inesperado: " + ex.getMessage());
                }
            }

            System.out.println("Saliendo. ¡Hasta luego!");
            scanner.close();
        }

        /**
         * Asegura la existencia del fichero de log (crea si no existe).
         */
        private void ensureLogFile() {
            try {
                File parent = logFile.getAbsoluteFile().getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs()) {
                        System.err.println("No se pudieron crear directorios: " + parent.getAbsolutePath());
                    }
                }
                if (!logFile.exists()) {
                    if (!logFile.createNewFile()) {
                        System.err.println("No se pudo crear el fichero de log: " + logFile.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al asegurar fichero de log: " + e.getMessage());
            }
        }

        /**
         * Cambia la codificación que se usará para leer/escribir el log.
         *
         * @param encoding nombre de la codificación (por ejemplo "UTF-8" o "ISO-8859-1")
         */
        public void setEncoding(String encoding) {
            if (encoding == null) return;
            this.encoding = encoding;
        }

        /**
         * Añade un evento al fichero con la marca temporal actual.
         *
         * @param message texto del evento
         * @throws IOException si hay error de E/S
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
         *
         * @param dateStr fecha en formato YYYY-MM-DD
         * @throws IOException            si hay error de lectura
         * @throws DateTimeParseException si el formato de fecha no es válido
         */
        public void filterByDate(String dateStr) throws IOException, DateTimeParseException {
            // validar formato de fecha
            DATE_ONLY_FMT.parse(dateStr); // lanza DateTimeParseException si no es válido
            String prefix = "[" + dateStr; // las líneas empiezan con [YYYY-MM-DD

            try (FileInputStream fis = new FileInputStream(logFile);
                 InputStreamReader isr = new InputStreamReader(fis, encoding);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                boolean any = false;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(prefix)) {
                        System.out.println(line);
                        any = true;
                    }
                }
                if (!any) {
                    System.out.println("No se encontraron eventos para la fecha " + dateStr);
                }
            }
        }

        /**
         * Muestra todo el log por consola (uso de ayuda / debug).
         *
         * @throws IOException si hay error de lectura
         */
        public void showAll() throws IOException {
            try (FileInputStream fis = new FileInputStream(logFile);
                 InputStreamReader isr = new InputStreamReader(fis, encoding);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }

}
