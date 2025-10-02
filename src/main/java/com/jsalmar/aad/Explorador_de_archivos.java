package com.jsalmar.aad;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Explorador_de_archivos {
 private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Introduce la ruta de un directorio: ");

        String ruta = sc.nextLine();

        File directorio = new File(ruta);

        if (!directorio.exists() || !directorio.isDirectory()) {
            System.out.println("El directorio no existe o no es un directorio");
            return;
        }

        Mostrar_Ficheros(directorio);

        int opciones;
        do {
            System.out.println("----Menu Principal----");
            System.out.println("1_Crear Fichero");
            System.out.println("2_Eliminar Fichero");
            System.out.println("3_Mover Fichero");
            System.out.println("4_Salir de la app (0-0)/");
            System.out.println("Elegir opcion: ");
            opciones = sc.nextInt();
            sc.nextLine();

            //Aqui va a ir un switch


        } while (opciones != 4);
    }


        private static void Mostrar_Ficheros (File directorio)
        {
            File[] ficheros = directorio.listFiles();

            if (ficheros != null || ficheros.length == 0)
            {
                System.out.println("Ficheros vacios...");
                return;
            }

            SimpleDateFormat horas = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            System.out.println("Contenido del directorio " + directorio.getAbsolutePath() + " : ");

            for (File fichero : ficheros)
            {
                String tipo = fichero.isDirectory() ? "Directorio" : "Contenido";
                String ultimaModificacion = horas.format(fichero.lastModified());
                long size = fichero.isFile() ? fichero.length() : 0;

                System.out.println(tipo + " " + fichero.getName() + " (" + size + " bytes) " + ultimaModificacion);
            }

        }

        private static void crear (File directorio)
        {
            System.out.print("Introduce el nombre del fichero: ");
            String nombre = sc.nextLine();
            File f_nuevo = new File(directorio, nombre);

            try
            {
                if (f_nuevo.createNewFile())
                {
                    System.out.println("Fichero " + f_nuevo.getAbsolutePath() + " creado");
                }
                else
                {
                    System.out.println("No se pudo crear el fichero " + f_nuevo.getAbsolutePath() + " porque ya existe");
                }
            }
            catch (IOException e)
            {
                System.out.println("No se pudo crear el fichero " + f_nuevo.getAbsolutePath() + " porque se produjo un error: " + e.getMessage());
            }
        }

        private static void moverF ()
        {
            System.out.print("Introduce la ruta de origen: ");
            String origen = sc.nextLine();
            System.out.print("Introduce la ruta de destino: ");
            String destino = sc.nextLine();

            File origen_f = new File(origen);
            File destino_f = new File(destino);

            if (origen_f.exists() && origen_f.isFile())
            {
                if (destino_f.exists() && destino_f.isFile())
                {
                    if (origen_f.renameTo(destino_f))
                    {
                        System.out.println("Fichero " + origen_f.getAbsolutePath() + " movido a " + destino_f.getAbsolutePath());
                    }
                    else
                    {
                        System.out.println("No se pudo mover el fichero " + origen_f.getAbsolutePath() + " a " + destino_f.getAbsolutePath());
                    }
                }
            }
        }

        private static void borrarF ()
        {
            System.out.println("Que fichero quieres borrar : ");
            String nombre = sc.nextLine();

            File fichero = new File(nombre);

            if (fichero.exists())
            {
                if (fichero.delete())
                {
                    System.out.println("Fichero " + fichero.getAbsolutePath() + " borrado");
                }
                else if (!fichero.delete())
                {
                    System.out.println("No se pudo borrar el fichero " + fichero.getAbsolutePath());
                }
            }
            else if (!fichero.exists())
            {
                System.out.println("El fichero " + fichero.getAbsolutePath() + " no existe lo siento...");
            }
        }




}

