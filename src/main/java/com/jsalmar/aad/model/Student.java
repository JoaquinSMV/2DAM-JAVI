package com.jsalmar.aad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {

    private Integer id;
    private String nif;
    private String name;
    private String email;
    private String curse;
    private List<Module> modules;


    // public Student(Integer id, String nif, String name, String email, String curse, List<Module> modules) { }
}