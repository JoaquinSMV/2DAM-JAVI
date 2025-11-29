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
import java.sql.Statement;
import java.util.Objects;

@Repository
@Slf4j
public class StudentJdbcRepository implements CrudRepository<Student> {

    private static final String SQL_INSERT = """
            INSERT INTO alumno (nif , nombre , email) VALUES (?, ?, ?)
            """;

    private static final String SQL_SELECT_BY_DNI = """
            SELECT id_alumno, nombre, nif, email FROM alumno WHERE nif = ?
            """;

    private static final String SQL_UPDATE = """
            UPDATE alumno SET nombre = ?, email = ? WHERE nif = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM alumno WHERE nif = ?
            """;
    private static final String SQL_SELECT_BY_ID = """
            SELECT id_alumno, nombre, nif, email FROM alumno WHERE id_alumno = ?
            """;
    //private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
        Student s = new Student();
        s.setId(rs.getInt("id_alumno"));
        s.setName(rs.getString("nombre"));
        s.setNif(rs.getString("nif"));
        s.setEmail(rs.getString("email"));
        return s;
    };


    public StudentJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Student create(Student entity) {
        if (entity == null) throw new IllegalArgumentException("Student cannot be null");

        //Voy a usar el KeyHolder para coger la id que se genere..

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = jdbcTemplate.update(conection ->
                {
                    PreparedStatement ps = conection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, entity.getNif());      // nif
                    ps.setString(2, entity.getName());     // nombre
                    ps.setString(3, entity.getEmail());    // email
                    return ps;

                }
                , keyHolder);


        // Obtener el ID generado
        if (affectedRows > 0) {
            entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.info("CREATED OK: {}", entity.getId());
            return entity;
        }

        throw new RuntimeException("Error creating Student: " + entity);
    }

    @Override
    public Student read(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("read requires a Student with non-null NIF");
        }

        try {

            Student found = jdbcTemplate.queryForObject(SQL_SELECT_BY_DNI, studentRowMapper, entity.getNif());
            log.info("Student found: {}", found);
            return found;

        } catch (EmptyResultDataAccessException e) {
            log.info("No student found with NIF={}", entity.getNif());
            return null;
        }
    }

    @Override
    public Student update(Student entity) {

        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("update requires a Student with non-null NIF");
        }

        int updated = jdbcTemplate.update(SQL_UPDATE,

                entity.getName(),     // nombre
                entity.getEmail(),    // email
                entity.getNif()       // nif

        );

        if (updated == 0) {
            throw new RuntimeException("Student not found for update: nif=" + entity.getNif());
        }

        log.info("Student updated: {}", entity);
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
            log.info("No student found with ID={}", id);
            return null;
        }

    }
}
