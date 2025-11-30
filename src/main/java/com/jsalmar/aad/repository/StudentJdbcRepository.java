package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
@Slf4j
public class StudentJdbcRepository implements CrudRepository<Student> {

    private static final String SQL_INSERT = """
            INSERT INTO "alumno" ("nif" , "nombre" , "email") VALUES (?, ?, ?)
            """;

    private static final String SQL_SELECT_BY_DNI = """
            SELECT "id_alumno", "nombre", "nif", "email" FROM "alumno" WHERE "nif" = ?
            """;

    private static final String SQL_UPDATE = """
            UPDATE "alumno" SET "nombre" = ?, "email" = ? WHERE "nif" = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM "alumno" WHERE "nif" = ?
            """;
    private static final String SQL_SELECT_BY_ID = """
            SELECT "id_alumno", "nombre", "nif", "email" FROM "alumno" WHERE "id_alumno" = ?
            """;
    //private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
        Student student = new Student();
        student.setId(rs.getInt("id_alumno"));
        student.setNif(rs.getString("nif"));
        student.setName(rs.getString("nombre"));
        student.setEmail(rs.getString("email"));
        return student;
    };


    public StudentJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -----------------------------
    // Métodos requeridos por CrudRepository
    // -----------------------------

    @Override
    public Student create(Student entity) {

        if (entity == null || entity.getName() == null || entity.getNif() == null) {
            throw new IllegalArgumentException("Student must have non-null name and NIF");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int inserted = jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(SQL_INSERT, new String[]{"id_alumno"});

            ps.setString(1, entity.getNif());
            ps.setString(2, entity.getName());
            ps.setString(3, entity.getEmail());
            return ps;
        }, keyHolder);

        if (inserted == 0) {
            throw new IllegalStateException("Insert failed, no rows affected");
        }

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Student created with ID: {}", entity.getId());

        return entity;
    }

    @Override
    public Student read(Student entity) {

        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("Read requires a Student with non-null NIF");
        }

        try {

            Student found = jdbcTemplate.queryForObject(SQL_SELECT_BY_DNI, studentRowMapper, entity.getNif());
            log.info("Student found by NIF: {}", found);
            return found;

        } catch (EmptyResultDataAccessException e) {

            log.warn("Student not found with NIF: {}", entity.getNif());
            return null;

        }
    }

    @Override
    public Student update(Student entity) {

        if (entity == null || entity.getName() == null || entity.getEmail() == null || entity.getNif() == null) {
            throw new IllegalArgumentException("Update requires Student with non-null name, email, and NIF");
        }

        int updated = jdbcTemplate.update(SQL_UPDATE,
                entity.getName(),
                entity.getEmail(),
                entity.getNif());

        boolean ok = updated > 0;
        log.info("Student update {} for nif={}. Updated: {}", ok ? "OK" : "NOOP", entity.getNif(), entity);
        return entity;

    }

    @Override
    public Student findAll(Student entity) {
        return null;
    }

    // -----------------------------
    // Método requerido por CrudRepository
    // -----------------------------

    @Override
    public boolean delete(Student entity) {

        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("Delete requires a Student with non-null NIF");
        }

        int deleted = jdbcTemplate.update(SQL_DELETE, entity.getNif());
        boolean ok = deleted > 0;

        log.info("Student delete {} for nif={}", ok ? "OK" : "NOOP", entity.getNif());
        return ok;

    }

    // -----------------------------
    // Auxiliar
    // -----------------------------

    @Override
    public boolean validate(Student entity) {
        return entity != null && entity.getName() != null && entity.getNif() != null;
    }

    public Student findById(Integer id) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try {

            Student found = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, studentRowMapper, id);
            log.info("Student found by ID: {}", found);
            return found;

        } catch (EmptyResultDataAccessException e) {

            log.warn("Student not found with ID: {}", id);
            return null;

        }

    }
}