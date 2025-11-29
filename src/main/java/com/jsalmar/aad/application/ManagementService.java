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

    // Se elimina la dependencia de PostgreSQLDriver ya que Spring gestiona la conexión/transacción.
    // private PostgreSQLDriver postgreSQLDriver;


    // Hacemos el constructor (sin PostgreSQLDriver)
    public ManagementService(StudentJdbcRepository studentRepository, ModuleRepository moduleRepository, EnrollMentRepository enrollMentRepository) {
        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.enrollMentRepository = enrollMentRepository;
    }

    //------------------------------------------------
    // Hacemos ahora tm una gestion de los modulos
    //------------------------------------------------

    @Transactional
    public Module createM(Module module) {

        if (module == null || module.getCode() == null || module.getName() == null) {

            throw new IllegalArgumentException("Module and name are required...");

        }

        Module existing = moduleRepository.findAll().stream()
                .filter(m -> module.getCode().equals(m.getCode()))
                .findFirst().orElse(null);

        if (existing != null) {

            log.error("Module already exists: {}", existing);

            return existing;

        }

        // Creamos el modulo

        Module created = moduleRepository.create(module);
        log.info("Module created: {}", created);

        return created;
    }

    //---------------------------------------
    //        GESTION DE ESTUDIANTES
    //---------------------------------------

    @Transactional // Usamos @Transactional para asegurar atomicidad en la creación de lo estudiantes
    public Student createS(Student student) {
        if (!studentRepository.validate(student)) {

            throw new IllegalArgumentException("Student and NIF are required...");

        }

        Student existing = studentRepository.read(student);

        if (existing != null) {

            log.error("Student already exist: {}", existing);

            return existing;
        }

        // creamos un nuevo studiante

        Student created = studentRepository.create(student);
        log.info("Student are ready: {}", created);

        return created;

    }


    //---------------------------------------------------------------
    //          PARA LA MATRICULACIÓN DE LOS ESTUDIANTES
    //--------------------------------------------------------------

    @Transactional // Spring gestionará la transacción: rollback si hay RuntimeException.
    public void enrollstudent(Integer studentId, Integer moduleId) {

        Student student = studentRepository.findById(studentId);

        if (student == null) {

            throw new RuntimeException("Student not found: " + studentId);

        }

        Module module = moduleRepository.findById(moduleId);

        if (module == null) {

            throw new RuntimeException("Module not found: " + moduleId);

        }

        // Creamos el objeto Enrollment

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);

        // enrollment.setModuleId(moduleId); // El EnrollmentRepository usa esto en el bucle, pero si lo comentoi no pasa na

        enrollment.setEnrollmentDate(LocalDate.now());
        enrollMentRepository.createEnrollment(enrollment, List.of(module));
        log.info("Student {} successfully enrolled in module {}", studentId, moduleId);

    }

    //---------------------------------------------------------------
    //          IMPLEMENTACIÓN DE CRUDREPOSITORY<MODULE>
    //--------------------------------------------------------------


    @Override
    @Transactional
    public Module create(Module entity) {

        return moduleRepository.create(entity);

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