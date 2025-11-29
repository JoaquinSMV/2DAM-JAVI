package com.jsalmar.aad.application;

//import com.jsalmar.aad.config.PostgreSQLDriver;

import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.CrudRepository;
import com.jsalmar.aad.repository.EnrollMentRepository;
import com.jsalmar.aad.repository.ModuleRepository;
import com.jsalmar.aad.repository.StudentJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ManagementService implements CrudRepository<Module> {

    private final StudentJdbcRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollMentRepository enrollMentRepository;
    //private PostgreSQLDriver postgreSQLDriver;


    //Hacemos el constructor
    public ManagementService(StudentJdbcRepository studentRepository, ModuleRepository moduleRepository, EnrollMentRepository enrollMentRepository) {

        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.enrollMentRepository = enrollMentRepository;
        //this.postgreSQLDriver = postgreSQLDriver;

    }

    //------------------------------------------------
    // Hacemos ahora tm una gestion de los modulos
    //------------------------------------------------


    public Module createM(Module module) {
        if (module == null || module.getCode() == null || module.getName() == null) {
            throw new IllegalArgumentException("Module and name are required...");
        }

        //Tendriamos que comprobar si existe un modulo igual , o que tenga el mismo codigo...

        Module existing = moduleRepository.findAll().stream().filter(m -> m.getCode().equals(m.getCode())).findFirst().orElse(null);

        if (existing != null) {
            log.error("Module already exists: {}", existing);
            return existing;
        }

        //Creamos el modulo

        Module created = moduleRepository.create(module);
        log.info("Module created: {}", created);
        return created;

    }

    //---------------------------------------
    //        GESTION DE ESTUDIANTES
    //---------------------------------------

    public Student createS(Student student) {
        if (!studentRepository.validate(student)) {
            throw new IllegalArgumentException("Student and NIF are required...");
        }
        //Igual que arriba pero ahora con estudiantes

        Student existing = studentRepository.read(student);

        if (existing != null) {
            log.error("Student already exist: {}", existing);
            return existing;
        }

        //creamos un nuevo studiante

        Student created = studentRepository.create(student);
        log.info("Student are ready: {}", created);
        return created;
    }


    //---------------------------------------------------------------
    //          PARA LA MATRICULACIÓN DE LOS ESTUDIANTES
    //--------------------------------------------------------------

    @Transactional
    public void enrollstudent(Integer studentId, Integer moduleId) {

        Student student = studentRepository.findById(studentId);

        if (student == null) {
            throw new RuntimeException("Student not found: " + studentId);
        }

        enrollMentRepository.createEnrollment(enrollment, List.of(module));
    }

    @Override
    public Module create(Module entity) {
        return null;
    }

    @Override
    public Module read(Module entity) {
        return null;
    }

    @Override
    public Module update(Module entity) {
        return null;
    }

    @Override
    public Module findAll(Module entity) {
        return null;
    }

    @Override
    public boolean delete(Module entity) {
        return false;
    }

    @Override
    public boolean validate(Module entity) {
        return entity != null;
    }
}
