package Actividad_1_2;

/*
 * Clase Alumno
 * Representa a un alumno con id, nombre y nota.
 * El nombre siempre ocupa 20 caracteres para que el registro sea fijo.
 * Sirve para guardar los alumnos en el fichero binario y manejar sus datos.
 */


// Clase que representa a un alumno con id, nombre y nota
public class Alumno {
    private int id;
    private String nombre;
    private double nota;

    // Define el tamaño fijo del nombre y del registro completo
    public static final int TAMANO_NOMBRE = 20;
    public static final int TAMANO_REGISTRO = Integer.BYTES + (TAMANO_NOMBRE * Character.BYTES) + Double.BYTES; // 4 + 40 + 8 = 52 bytes

    public Alumno() {
        this.id = 0;
        this.nombre = "";
        this.nota = 0.0;
    }

    public Alumno(int id, String nombre, double nota) {
        this.id = id;
        setNombre(nombre);
        this.nota = nota;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getNota() {
        return nota;
    }

    public void setId(int id) {
        this.id = id;
    }

    // El nombre siempre tiene que ocupar 20 caracteres para que el registro sea de tamaño fijo
    public void setNombre(String nombre) {
        if (nombre == null) {
            nombre = "";
        }

        if (nombre.length() > TAMANO_NOMBRE) {
            this.nombre = nombre.substring(0, TAMANO_NOMBRE);
        } else {
            // Rellena con espacios si el nombre es mas corto de 20 caracteres
            this.nombre = String.format("%-" + TAMANO_NOMBRE + "s", nombre);
        }
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s | Nota: %.2f", id, nombre.trim(), nota);
    }
}