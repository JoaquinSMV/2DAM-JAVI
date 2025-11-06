package com.jsalmar.aad;

import com.fencgut961.aad.application.StudentService;
import com.fencgut961.aad.model.Module;
import com.fencgut961.aad.model.Student;
import com.fencgut961.aad.util.Constant;
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
        Module module1 = new Module("CS101", "Introduction to Computer Science");
        Module module2 = new Module("CS102", "Data Structures");
        List<Module> modules = List.of(module1, module2);
        Student create = studentService.createStudent(vito, modules);
        if (create != null) {
            log.info("Create: {}", create);
        } else {
            log.error(Constant.STUDENT_NOT_FOUND);
        }
    }
}
