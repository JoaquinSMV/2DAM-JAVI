package com.jsalmar.aad.model;

import lombok.Data;
import lombok.ToString;

import java.lang.Module;
import java.util.List;

@Data
@ToString
public class Student extends Person {

    private String curse;
    private List<Module> modules;

    public Student(String dni, String name, String surname) {
        super(dni, name, surname);
    }

    public Student(String dni, String name, String surname, String curse) {
        super(dni, name, surname);
        this.curse = curse;
    }

}
