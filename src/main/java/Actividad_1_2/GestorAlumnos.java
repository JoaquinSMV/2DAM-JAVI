package Actividad_1_2;

import java.io.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/*

Esto lo voy a comentar para en el futuro acordarme y saber que hace cada cosa , por eso seguramente cada linea de codigo que
no haya usado antes va a estar comentada.

 */

public class GestorAlumnos {
    private String fichero;

    public GestorAlumnos(String fichero) {
        this.fichero = fichero;
    }

    // Añade un alumno al final del fichero (acceso secuencial)
    public boolean insertarAlumno(Alumno alumno) {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
            // Se posiciona al final para escribir ahi
            raf.seek(raf.length()); //Se posiciona al final del fichero
            escribirAlumno(raf, alumno); //Escribe el alumno en el fichero
            System.out.println("Alumno insertado correctamente");
            return true;
        } catch (IOException e) {
            System.err.println("Error al insertar alumno: " + e.getMessage());
            return false;
        }
    }

    // Lee un alumno directamente por su posicion (acceso aleatorio)
    public Alumno consultarAlumnoPorPosicion(int pos) {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
            // Calcula donde empieza ese alumno en el fichero
            long offset = (long) pos * Alumno.TAMANO_REGISTRO;

            // Sirve para evitar que intentemos leer de una posicion fuera del fichero
            if (offset >= raf.length()) {
                System.out.println("La posicion " + pos + " no existe");
                return null;
            }

            // Salta directamente a esa posicion
            raf.seek(offset);
            Alumno alumno = leerAlumno(raf);//Leemos el alumno desde esa posicion
            System.out.println("Alumno consultado: " + alumno);
            return alumno;
        } catch (IOException e) {
            System.err.println("Error al consultar alumno: " + e.getMessage());
            return null;
        }
    }

    // Cambia solo la nota de un alumno sin tocar el resto (acceso aleatorio)
    public boolean modificarNota(int pos, double nuevaNota) {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
            long offset = (long) pos * Alumno.TAMANO_REGISTRO; //Calcula donde empieza ese alumno en el fichero

            if (offset >= raf.length()) {
                System.out.println("La posicion " + pos + " no existe");
                return false;
            }

            // Se posiciona justo donde esta la nota: despues del id y el nombre
            long offsetNota = offset + Integer.BYTES + (Alumno.TAMANO_NOMBRE * Character.BYTES);
            raf.seek(offsetNota);//Salta a esa posicion
            raf.writeDouble(nuevaNota);//Escribe la nueva nota

            System.out.println("Nota modificada correctamente a: " + nuevaNota);
            return true;
        } catch (IOException e) {
            System.err.println("Error al modificar nota: " + e.getMessage());
            return false;
        }
    }

    // Muestra todos los alumnos que hay en el fichero
    public void listarAlumnos() {
        File f = new File(fichero);

        if (!f.exists()) {
            System.out.println("El fichero no existe. No hay alumnos registrados.");
            return;
        }


        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
            int total = (int) (raf.length() / Alumno.TAMANO_REGISTRO);

            if (total == 0) {
                System.out.println("No hay alumnos en el fichero.");
                return;
            }

            System.out.println("\n========== LISTADO DE ALUMNOS ==========");
            System.out.println("Total: " + total);
            System.out.println("----------------------------------------");

            for (int i = 0; i < total; i++) {
                raf.seek((long) i * Alumno.TAMANO_REGISTRO);
                Alumno alumno = leerAlumno(raf);
                System.out.println("Posicion " + i + ": " + alumno);
            }
            System.out.println("========================================\n");
        } catch (IOException e) {
            System.err.println("Error al listar alumnos: " + e.getMessage());
        }
    }

    // Devuelve cuantos alumnos hay guardados
    public int obtenerNumeroAlumnos() {
        File f = new File(fichero);
        if (!f.exists()) return 0;

        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
            return (int) (raf.length() / Alumno.TAMANO_REGISTRO);
        } catch (IOException e) {
            System.err.println("Error al obtener numero de alumnos: " + e.getMessage());
            return 0;
        }
    }

    // Escribe los datos de un alumno en el fichero
    private void escribirAlumno(RandomAccessFile raf, Alumno alumno) throws IOException {
        raf.writeInt(alumno.getId());

        String nom = alumno.getNombre();
        for (int i = 0; i < Alumno.TAMANO_NOMBRE; i++) {
            raf.writeChar(nom.charAt(i));
        }

        raf.writeDouble(alumno.getNota());
    }

    // Lee los datos de un alumno desde el fichero
    private Alumno leerAlumno(RandomAccessFile raf) throws IOException {
        int id = raf.readInt(); //Leemos el id del alumno desde el fichero y lo guardamos en el objeto alumno

        StringBuilder nom = new StringBuilder(); //Creamos un stringbuilder para almacenar el nombre del alumno ( stringbuilder sirve para leer los caracteres desde el fichero y dps almacenarlos )
        for (int i = 0; i < Alumno.TAMANO_NOMBRE; i++) {
            nom.append(raf.readChar()); // Leemos un caracter del fichero y lo agregamos al stringbuilder
        }

        double nota = raf.readDouble();
        return new Alumno(id, nom.toString(), nota);
    }

    // Borra el fichero de alumnos
    public boolean eliminarFichero() {
        File f = new File(fichero);
        if (f.exists()) {
            return f.delete();// Si existe borra el fichero
        }
        return true;
    }
}

