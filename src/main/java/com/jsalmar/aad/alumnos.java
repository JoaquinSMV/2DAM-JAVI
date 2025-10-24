package com.jsalmar.aad;

import lombok.Getter;
import lombok.Setter;

/*
 * Clase alumnos
 * -------------
 * Representa el modelo de datos de un alumno.
 * Contiene tres atributos: id, nombre y nota.
 *
 * Usa Lombok para generar automáticamente los métodos getter y setter.
 */

@Setter
@Getter
public class alumnos {

    int id;
    String nombre;
    double nota;

    public alumnos(int id, String nombre, double nota) {
        this.id = id;
        this.nombre = nombre;
        this.nota = nota;
    }
}
