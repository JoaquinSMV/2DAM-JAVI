package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//ctrl + mayus + r

@Repository
@Slf4j
public class StudentJdbcRepository implements CrudRepository<Student> {


    private static final String SQL_INSERT = """
            INSERT INTO alumno (nombre, email)
            VALUES (?, ?)
            """;

    private static final String SQL_SELECT_BY_DNI = """
            SELECT id_alumno, nombre, email
                FROM alumno
                WHERE id_alumno = ?
            """;

    private static final String SQL_UPDATE = """
            UPDATE alumno
            SET nombre = ?
            WHERE email = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM alumno
            WHERE email = ?
            """;

    private final DataSource dataSource;

    public StudentJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Student create(Student entity) {
        if (entity == null) throw new IllegalArgumentException("Student cannot be null");
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDni()); // DNI va en email

            ps.executeUpdate();

            log.info("create OK: {}", entity);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Student", e);
        }
    }

    @Override
    public Student read(Student entity) {
        // getId -> getDni
        if (entity == null || entity.getDni() == null) {
            throw new IllegalArgumentException("read requires a Student with non-null dni");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_DNI)) {

            ps.setString(1, entity.getDni());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student found = mapRow(rs);
                    // Preserve surname from input (DB has no surname)
                    found.setSurname(entity.getSurname()); // setLastName -> setSurname (el bueno)
                    log.info("read OK: {}", found);
                    return found;
                } else {
                    log.info("read: no student found with dni={}", entity.getDni());
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading Student dni=" + entity.getDni(), e);
        }
    }

    @Override
    public Student update(Student entity) {
        // getId -> getDni
        if (entity == null || entity.getDni() == null) {
            throw new IllegalArgumentException("update requires a Student with non-null dni");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            // setFirstName -> setName (using getter for update)
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDni());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Student not found for update: dni=" + entity.getDni());
            }
            log.info("update OK: {}", entity);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Student dni=" + entity.getDni(), e);
        }
    }

    @Override
    public boolean delete(Student entity) {
        // getId -> getDni
        if (entity == null || entity.getDni() == null) {
            throw new IllegalArgumentException("delete requires a Student with non-null dni");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setString(1, entity.getDni());
            int deleted = ps.executeUpdate();
            boolean ok = deleted > 0;
            log.info("delete {} for dni={}", ok ? "OK" : "NOOP", entity.getDni());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Student dni=" + entity.getDni(), e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        String email = rs.getString("email");  // El DNI está en email
        String name = rs.getString("nombre");
        String surname = ""; // not persisted
        return new Student(email, name, surname);
    }
}
