package com.jsalmar.aad;

import lombok.Getter;
import lombok.Setter;

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
