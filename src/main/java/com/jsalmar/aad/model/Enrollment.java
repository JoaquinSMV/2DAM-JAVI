package com.jsalmar.aad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // genera constructor vacío
@AllArgsConstructor // genera constructor con todos los campos
public class Enrollment {
    private Integer id;
    private Integer studentId;
    private Integer moduleId;
    private LocalDate enrollmentDate;
}
