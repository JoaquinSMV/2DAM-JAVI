package com.jsalmar.aad;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Profile;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.EnrollMentRepository;
import com.jsalmar.aad.repository.StudentRepository;
import com.jsalmar.aad.service.ManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class AadApplication implements CommandLineRunner {

    // Esto inyecta estas instancias automáticamente
    private final ManagementService managementService;
    private final EnrollMentRepository enrollMentRepository;
    private final StudentRepository studentRepository;

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando prueba de transacciones...");

        // 1. Crear Perfil
        Profile perfil = new Profile();
        perfil.setAddress("Calle Falsa 123");
        perfil.setPhone("600112233");

        // 2. Crear Alumno (Miriam)
        Student miriam = new Student();
        miriam.setNif("66280457T");
        miriam.setName("Miriam");
        miriam.setEmail("miriam@g.educaand.es");
        miriam.setCurse("DAW");
        miriam.setProfile(perfil);

        // 3. Crear Módulo (Programación)
        Module programacion = new Module();
        programacion.setCode("0485");
        programacion.setName("Programación");
        programacion.setHours(250);

        // Ejecución de lógica
        miriam = managementService.createStudent(miriam);
        log.info("Alumno creado: {}", miriam);

        programacion = managementService.createModule(programacion);
        log.info("Módulo creado: {}", programacion);

        Enrollment enrollment = managementService.enrollStudentInModule(miriam.getId(), programacion.getId());
        log.info("Matrícula realizada: {}", enrollment);

        int countEnrollments = managementService.countEnrollments(miriam.getId());
        log.info("{} módulos matriculados para el alumno {}", countEnrollments, miriam.getName());

        // Prueba de recuperación y borrado
        miriam = studentRepository.findByNif(miriam.getNif())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        log.info("Alumno recuperado: {}", miriam);

        studentRepository.delete(miriam);
        log.info("Alumno {} eliminado", miriam.getName());

        // EL MOMENTO CLAVE:
        throw new RuntimeException("Forzando rollback de la transacción");
    }
}