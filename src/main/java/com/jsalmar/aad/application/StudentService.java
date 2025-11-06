package com.jsalmar.aad.application;

import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.ModuleRepository;
import com.jsalmar.aad.repository.StudentJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService implements CustomService<Student> {

    private final StudentJdbcRepository studentRepository;
    private final ModuleRepository moduleRepository;

//    public StudentService(StudentRepository studentRepository) {
//        this.studentRepository = studentRepository;
//    }

    /**
     * @param entity
     * @return
     */
    @Override
    public boolean validate(Student entity) {
        return !entity.getDni().isBlank() && !entity.getName().isBlank();
    }

    public Student createStudent(final Student student, final List<Module> modules) {
        if (validate(student)) {
            student.setModules(modules);
            return studentRepository.create(student);
        }
        return null;
    }
}
