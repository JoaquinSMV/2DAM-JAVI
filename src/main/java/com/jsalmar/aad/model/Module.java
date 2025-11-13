package com.jsalmar.aad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Module {

    private String code;
    private String name;
    private Integer id;
    private Integer hours;
}
