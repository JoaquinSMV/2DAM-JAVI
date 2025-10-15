package Actividad_1_2;



import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/*
 * Clase GestorAlumnado
 * Esta clase se encarga de todo lo relacionado con el fichero de alumnos.
 * Aquí se leen y escriben los alumnos en el fichero binario,
 * Se puede insertar, consultar, modificar notas, listar todos
 * y borrar el fichero si hace falta.
 * Todo con RandomAccessFile para que los registros sean de tamaño fijo.
 */


public class GestorAlumnado {

    private static Scanner sc = new Scanner(System.in);
    private String fichero;

    public GestorAlumnado(String fichero) {
        this.fichero = fichero;
    }

   /*
   Ahora hay que crear metodos para -insertar, -listar, -modificar(Aqui investigare un poco), -el nº de alumnos
   -Consultar , -escribir alumno , -leer alumno y -borrar fichero , todo eso
    */

    private void escribirAlumno(RandomAccessFile raf, Alumno alumno) throws IOException {
        raf.writeInt(alumno.getId());

        String nom = alumno.getNombre();
        for (int i = 0; i < Alumno.TAMANO_NOMBRE; i++) {
            raf.writeChar(nom.charAt(i));
        }

        raf.writeDouble(alumno.getNota());
    }

    private Alumno leerA(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        StringBuilder nom = new StringBuilder();
        for (int i = 0; i < Alumno.TAMANO_NOMBRE; i++) {
            nom.append(raf.readChar());
        }
        double nota = raf.readDouble();
        Alumno alumno = new Alumno(id, nom.toString(), nota);
        System.out.println(alumno);
        return alumno;
    }
    
    public boolean borrarFichero() {
        File f = new File(fichero);
        if (f.exists()) {
            return f.delete();
        } else {
            System.out.println("El fichero no existe");
            return false;
        }
    }
    
    public int obtenerNumeroAlumnos()
    {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
            return (int) (raf.length() / Alumno.TAMANO_REGISTRO);
        } catch (IOException e) {
            System.err.println("Error al obtener numero de alumnos: " + e.getMessage());
            return 0;
        }
    }

    //------------------------------------------------------------------------------------------------

    public boolean insertarAlumno(Alumno alumno) {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
            raf.seek(raf.length());
            escribirAlumno(raf, alumno);
            System.out.println("Se inserto el alumno: " + alumno + " en el fichero");
            return true;

        }  catch (IOException e) {
            System.err.println("Error al insertar el " + alumno + " : " + e.getMessage());
            return false;
        }
    }


    public void listarAlumnos()
    {
        File fch = new File(fichero);

        //Tenemos que ver si el fichero existe
        if (!fch.exists()) {
            System.out.println("El fichero no existe. No hay alumnos registrados.");
            return;
        }

        try(RandomAccessFile raf = new RandomAccessFile(fichero, "r"))
        {

            int totalB = (int) (raf.length() / Alumno.TAMANO_REGISTRO);

            if (totalB == 0)
            {
                System.out.println("No hay alumnos aun en el fichero.");
            }

            System.out.println("          Listado De Alumnos          ");
            System.out.println("Total: " + totalB);

            //Hacemos un bucle para

            for (int i = 0 ; i < totalB ; i++)
            {
                raf.seek((long) i * Alumno.TAMANO_REGISTRO);
                Alumno alumno = leerA(raf);
                System.out.println("Alumno " + i + ": " + alumno);
            }


        } catch (IOException e) {
            System.err.println("Error al listar los alumnos: " + e.getMessage());
        }

    }

    public Alumno consultarF (int possicion)
    {
        try (RandomAccessFile raf = new RandomAccessFile(fichero,"r"))
        {
            long offset = (long)possicion * Alumno.TAMANO_REGISTRO;
            if (offset >= raf.length())
            {
                System.out.println("La posicion " + possicion + " no existe");
                return null;
            }
            raf.seek(offset);
            Alumno alumno = leerA(raf);
            System.out.println("El alumno es: " + alumno);
            return alumno;
        }
        catch (IOException e)
        {
            System.err.println("Error al consultar el alumno: " + e.getMessage());
            return null;
        }
    }


    public boolean modificarNota (int pos, double nuevaNota)
    {
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
            long offset = (long) pos * Alumno.TAMANO_REGISTRO; 

            if (offset >= raf.length()) {
                System.out.println("La posicion " + pos + " no existe");
                return false;
            }

            
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


}
