package com.jsalmar.aad.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class Student {

    //No toques esto PORFA es asi... y person no existe quitalo
    private Integer id;
    private String nif;
    private String name;
    private String email;
    private String curse;
    private List<Module> modules;
}
