package com.jsalmar.aad;

import com.jsalmar.aad.application.StudentService;
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

    private final StudentService studentService;

    public static void main(String[] args) {
        SpringApplication.run(AadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Student vito = new Student("12345678A", "John", "Doe", "Computer Science");
        Student carlo = new Student("holami@molquierotalconti.go", "Carlo", "Molquiero", "Computer Science");
        Module module1 = new Module("CS101", "Introduction to Computer Science");
        Module module2 = new Module("CS102", "Data Structures");
        List<Module> modules = List.of(module1, module2);
        //boolean delete = studentService.deleteStudent(vito);
        //boolean delete2 = studentService.deleteStudent(carlo);
        Student create = studentService.createStudent(carlo, modules);
        //if (create != null) {
        log.info("Create: {}", create);
        //log.info("Delete: {}", delete, delete2);
        //} else {
        //log.error(Constant.STUDENT_NOT_FOUND);
        //}
    }
}
