package com.jsalmar.aad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "enrollments")
@Entity
@Table(name = "Student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Integer id;

    private String nif;

    @Column(name = "nombre")
    private String name;

    private String email;

    @Column(name = "curso")
    private String curse;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_perfil")
    private Profile profile;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;
}