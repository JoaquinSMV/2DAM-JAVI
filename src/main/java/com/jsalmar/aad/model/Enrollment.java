package com.jsalmar.aad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // genera constructor vacío
@AllArgsConstructor // genera constructor con todos los campos
@Entity
@Table(name = "Enrollment") //Esta tabla es intermedia hay que unir con la tabla alumno y la tabla modulo
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_alumno")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_modulo")
    private Module module;

    @Column(name = "fecha")
    private LocalDate enrollmentDate;

    @Column(name = "nota_final")
    private Double finalGrade;

}
