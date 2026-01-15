package com.jsalmar.aad.application;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.CrudRepository;
import com.jsalmar.aad.repository.EnrollMentRepository;
import com.jsalmar.aad.repository.ModuleRepository;
import com.jsalmar.aad.repository.StudentJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ManagementService implements CrudRepository<Module> {

    private final StudentJdbcRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollMentRepository enrollMentRepository;

    public ManagementService(StudentJdbcRepository studentRepository,
                             ModuleRepository moduleRepository,
                             EnrollMentRepository enrollMentRepository) {
        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.enrollMentRepository = enrollMentRepository;
    }

    //------------------------------------------------
    // GESTIÓN DE MATRICULACIÓN (Punto clave)
    //------------------------------------------------

    @Transactional // <--- Crucial: Si falla la inserción en el repo, se hace rollback
    public void enrollstudent(int studentId, int moduleId) {
        // 1. Verificar que el módulo existe
        Module module = moduleRepository.findById(moduleId);
        if (module == null) {
            log.error("Error al matricular: Módulo {} no encontrado", moduleId);
            throw new RuntimeException("Module not found");
        }

        // 2. Crear objeto de matriculación
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setEnrollmentDate(LocalDate.now());

        // 3. Persistir a través del repositorio de matrículas
        enrollMentRepository.createEnrollment(enrollment, List.of(module));

        log.info("Estudiante {} matriculado con éxito en el módulo {}", studentId, moduleId);
    }

    //------------------------------------------------
    // GESTIÓN DE ALUMNOS (Invocando a StudentJdbcRepository)
    //------------------------------------------------

    @Transactional
    public Student createS(Student student) {
        log.info("Creando estudiante: {}", student.getName());
        return studentRepository.create(student);
    }

    public Student findStudentByNif(String nif) {
        return studentRepository.read(new Student(null, nif, null, null, null, null));
    }

    //---------------------------------------------------------------
    //          IMPLEMENTACIÓN DE CRUDREPOSITORY<MODULE>
    //--------------------------------------------------------------

    @Override
    @Transactional
    public Module create(Module entity) {
        log.info("Creando módulo: {}", entity.getName());
        return moduleRepository.create(entity);
    }

    // Método alias para claridad
    @Transactional
    public Module createM(Module module) {
        return create(module);
    }

    @Override
    public Module read(Module entity) {
        if (entity.getId() != null) {
            return moduleRepository.findById(entity.getId());
        }
        return null;
    }

    @Override
    @Transactional
    public Module update(Module entity) {
        return moduleRepository.update(entity);
    }

    @Override
    public Module findAll(Module entity) {
        // Implementación según necesidad de la interfaz
        return null;
    }

    public List<Module> findAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    @Transactional
    public boolean delete(Module entity) {
        if (entity.getId() != null) {
            return moduleRepository.deleteById(entity.getId());
        }
        return false;
    }

    @Override
    public boolean validate(Module entity) {
        return moduleRepository.validate(entity);
    }
}