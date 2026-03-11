package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface EnrollMentRepository extends JpaRepository<Enrollment, Integer> {

    // Consulta para buscar matrículas por el NIF del alumno

    @Query("SELECT e FROM Enrollment e WHERE e.student.nif = :nif")
    List<Enrollment> findByStudentNif(@Param("nif") String nif);

    @Query("SELECT e FROM Enrollment e WHERE e.finalGrade >= :minGrade")
    List<Enrollment> findByMinFinalGrade(@Param("minGrade") Double minGrade);

}