package com.jsalmar.aad;

import com.jsalmar.aad.application.ManagementService;
import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
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

    private final ManagementService managementService;


    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Student miriam = new Student(null, "66280457T", "Miriam",
                "miriam@g.educaand.es", "DAW", List.of());
        Module programacion = new Module(null, "0485", "Programación", 250);

        miriam = managementService.createS(miriam);
        programacion = managementService.createM(programacion);
        managementService.enrollstudent(miriam.getId(), programacion.getId());

        // Para eliminar necesito inyectar StudentJdbcRepository
        // studentRepository.delete(miriam);
    }
}