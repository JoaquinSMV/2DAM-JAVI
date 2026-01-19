package com.jsalmar.aad.service;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.EnrollMentRepository;
import com.jsalmar.aad.repository.ModuleRepository;
import com.jsalmar.aad.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagementService {

    // Ahora inyectamos las interfaces de JPA
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollMentRepository enrollMentRepository;

    @Transactional
    public Student createS(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Module createM(Module module) {
        return moduleRepository.save(module);
    }

    @Transactional
    public void enrollStudent(Integer studentId, Integer moduleId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setFinalGrade(0.0);

        enrollMentRepository.save(enrollment);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
}