package com.jsalmar.aad;

import com.jsalmar.aad.model.Module;
import com.jsalmar.aad.model.Student;
import com.jsalmar.aad.repository.EnrollMentRepository;
import com.jsalmar.aad.service.ManagementService;
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

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando aplicación con JPA...");

        // Crear objetos
        Student student = new Student();
        student.setNif("12345678X");
        student.setName("Carmen");
        student.setEmail("Carmen@correo.com");
        student.setCurse("DAM");

        Module module = new Module();
        module.setCode("0485");
        module.setName("Programación");
        module.setHours(250);

        // Para guardar en bbdd
        student = managementService.createS(student);
        module = managementService.createM(module);

        // Para matricular al estudiante
        managementService.enrollStudent(student.getId(), module.getId());

        log.info("Se han creado los objetos en la BBDD , ole ole.");
    }
}