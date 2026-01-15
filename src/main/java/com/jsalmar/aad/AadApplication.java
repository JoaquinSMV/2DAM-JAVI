package com.jsalmar.aad;

import com.jsalmar.aad.application.ManagementService;
import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.EnrollMentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class AadApplication implements CommandLineRunner {

    // Spring inyecta estas instancias automáticamente
    private final ManagementService managementService;
    private final EnrollMentRepository enrollMentRepository;

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Datos de prueba
        Student miriam = new Student(null, "66280457T", "Miriam",
                "miriam@g.educaand.es", "DAW", List.of());
        Module programacion = new Module(null, "0485", "Programación", 250);

        // 2. Persistencia
        miriam = managementService.createS(miriam);
        programacion = managementService.createM(programacion);

        // 3. Matrícula
        managementService.enrollstudent(miriam.getId(), programacion.getId());

        // 4. LLAMADA AL PROCEDIMIENTO (Punto K de la rúbrica)
        // Al NO ser estático, aquí puedes usar enrollMentRepository sin errores
        int total = enrollMentRepository.countEnrollmentsByProcedure(miriam.getId());

        log.info("--------------------------------------------------");
        log.info("PUNTO K: El procedimiento indica que {} tiene {} matrícula(s)", miriam.getName(), total);
        log.info("--------------------------------------------------");
    }
}