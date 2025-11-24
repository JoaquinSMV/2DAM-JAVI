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
        // Crear estudiante
        Student miriam = new Student();
        miriam.setNif("66280457T");
        miriam.setName("Miriam");
        miriam.setEmail("miriam@g.educaand.es");
        miriam.setCurse("DAW");
        miriam.setModules(List.of());

        // Crear módulo
        Module programacion = new Module();
        programacion.setCode("0485");
        programacion.setName("Programación");
        programacion.setHours(250);

        // Usar el ManagementService para persistir
        miriam = managementService.createS(miriam);
        programacion = managementService.createM(programacion);

        // Matricular estudiante en módulo
        managementService.enrollstudent(miriam.getId(), programacion.getId());

        log.info("Prueba rápida completada: estudiante y módulo creados y matrícula realizada.");
    }
}