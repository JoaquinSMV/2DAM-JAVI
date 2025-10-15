package Actividad_1_2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Scanner;
import java.util.InputMismatchException;

@SpringBootApplication
public class Main implements CommandLineRunner {
    private static final String FICHERO = "alumnos.dat";
    private static GestorAlumnado gestor; //
    private static Scanner sc;


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    public void run (String...args) throws IOException {
        gestor = new GestorAlumnado(FICHERO);
        sc = new Scanner(System.in);

        System.out.println("------------------------------------------------");
        System.out.println("   GESTION DE NOTAS DE ALUMNOS");
        System.out.println("   Acceso Secuencial y Aleatorio");
        System.out.println("------------------------------------------------");

        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    insertarAlumno();
                    break;
                case 2:
                    consultarAlumno();
                    break;
                case 3:
                    modificarNota();
                    break;
                case 4:
                    listarAlumnos();
                    break;
                case 5:
                    mostrarInfo();
                    break;
                case 6:
                    eliminarFichero();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\nCerrando aplicacion...");
                    break;
                default:
                    System.out.println("\nOpcion no valida.\n");
            }

            if (!salir && opcion >= 0 && opcion <= 6) {
                esperarEnter();
            }
        }

        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println("----------------------------------------------");
        System.out.println("              MENU PRINCIPAL                  ");
        System.out.println("----------------------------------------------");
        System.out.println(" 1- Insertar nuevo alumno");
        System.out.println(" 2- Consultar alumno por posicion");
        System.out.println(" 3- Modificar nota de un alumno");
        System.out.println(" 4- Listar todos los alumnos");
        System.out.println(" 5- Informacion del fichero");
        System.out.println(" 6- Eliminar fichero de datos");
        System.out.println(" 0- Salir");
        System.out.println("----------------------------------------------");
        System.out.print("Elige una opcion: ");
    }

    private static int leerOpcion() {
        try {
            int op = sc.nextInt();
            sc.nextLine();
            return op;
        } catch (InputMismatchException e) {
            sc.nextLine();
            return -1;
        }
    }

    private static void insertarAlumno() {
        System.out.println("\n---- INSERTAR NUEVO ALUMNO ----");

        try {
            System.out.print("ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Nombre (maximo 20 caracteres): ");
            String nombre = sc.nextLine();

            System.out.print("Nota entre 0-10: ");
            double nota = sc.nextDouble();
            sc.nextLine();

            if (nota < 0 || nota > 10) {
                System.out.println("La nota debe estar entre 0 y 10");
                return;
            }

            Alumno alumno = new Alumno(id, nombre, nota);
            gestor.insertarAlumno(alumno);
        } catch (InputMismatchException e) {
            System.out.println("Error: Datos no bien introducidos");
            sc.nextLine();
        }
    }

    private static void consultarAlumno() {
        System.out.println("\n--- CONSULTAR ALUMNO POR POSICION ---");

        int total = gestor.obtenerNumeroAlumnos();
        if (total == 0) {
            System.out.println("No hay alumnos registrados.");
            return;
        }

        System.out.println("Posiciones disponibles: 0 a " + (total - 1));

        try {
            System.out.print("Posicion: ");
            int pos = sc.nextInt();
            sc.nextLine();

            if (pos < 0) {
                System.out.println("La posicion no puede ser negativa");
                return;
            }

            gestor.consultarF(pos); //
        } catch (InputMismatchException e) {
            System.out.println("Error: debe introducir un numero");
            sc.nextLine();
        }
    }

    private static void modificarNota() {
        System.out.println("---- MODIFICAR NOTA DE ALUMNO ----");

        int total = gestor.obtenerNumeroAlumnos();

        if (total == 0) {
            System.out.println("No hay alumnos registrados.");
            return;
        }

        System.out.println("Posiciones disponibles: 0 a " + (total - 1));

        try {
            System.out.print("Posicion del alumno: ");
            int pos = sc.nextInt();
            sc.nextLine();

            if (pos < 0) {
                System.out.println("La posicion no puede ser negativa");
                return;
            }

            System.out.println("\nAlumno actual:");
            Alumno alumno = gestor.consultarF(pos);
            if (alumno == null) return;

            System.out.print("\nNueva nota entre 0 y 10: ");
            double nuevaNota = sc.nextDouble();
            sc.nextLine();

            if (nuevaNota < 0 || nuevaNota > 10) {
                System.out.println("La nota debe estar entre 0 y 10");
                return;
            }

            gestor.modificarNota(pos, nuevaNota);

        } catch (InputMismatchException e) {
            System.out.println("Error: vuelve a introducir los datos");
            sc.nextLine();
        }
    }

    private static void listarAlumnos() {
        gestor.listarAlumnos();
    }

    private static void mostrarInfo() {
        System.out.println("\n--- INFORMACION DEL FICHERO ---");
        System.out.println("Nombre: " + FICHERO);
        System.out.println("Tamaño de cada registro: " + Alumno.TAMANO_REGISTRO + " bytes");
        System.out.println("  - ID: " + Integer.BYTES + " bytes");
        System.out.println("  - Nombre: " + (Alumno.TAMANO_NOMBRE * Character.BYTES) + " bytes");
        System.out.println("  - Nota: " + Double.BYTES + " bytes");

        int total = gestor.obtenerNumeroAlumnos();
        System.out.println("\nAlumnos registrados: " + total);
        System.out.println("Tamaño total: " + (total * Alumno.TAMANO_REGISTRO) + " bytes");

        System.out.println("\n--- Ventajas del acceso aleatorio ---");
        System.out.println("- Se puede ir directamente a cualquier alumno");
        System.out.println("- No hace falta leer todo el fichero");
        System.out.println("- Se puede modificar solo un campo");
        System.out.println("\nFormula: posicion_bytes = numero_alumno * " + Alumno.TAMANO_REGISTRO);
    }

    private static void eliminarFichero() {
        System.out.println("\n--- ELIMINAR FICHERO ---");
        System.out.print("Seguro que quieres eliminar el fichero? (S / N): ");
        String conf = sc.nextLine();

        if (conf.equalsIgnoreCase("S")) {
            if (gestor.borrarFichero()) {
                System.out.println("Fichero eliminado");
            } else {
                System.out.println("No se pudo eliminar");
            }
        } else {
            System.out.println("Cancelado");
        }
    }

    private static void esperarEnter() {
        System.out.println("\nPresiona Enter para continuar...");
        sc.nextLine();
    }
}
