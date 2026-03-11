package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface StudentRepository extends JpaRepository<Student, Integer> {
    // Este Spring es pa crear automticamente metodos como save, findAll, deleteById...
    Optional<Student> findByNif(String nif);
}