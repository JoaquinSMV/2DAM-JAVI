package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Slf4j
public class StudentJdbcRepository implements CrudRepository<Student> {

    private static final String SQL_INSERT = """
            INSERT INTO alumno (nombre, nif, email) VALUES (?, ?, ?)
            """;

    private static final String SQL_FIND_ALL = """
            SELECT nombre, nif, email FROM alumno = ?
            """;

    private static final String SQL_SELECT_BY_DNI = """
            SELECT nombre, nif, email FROM alumno WHERE nif = ?
            """;

    private static final String SQL_UPDATE = """
            UPDATE alumno SET nombre = ? WHERE nif = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM alumno WHERE nif = ?
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
            ps.setString(2, entity.getNif());

            String email = entity.getEmail() != null
                    ? entity.getEmail()
                    : ((entity.getNif() != null ? entity.getNif() : "unknown") + "@example.com");
            ps.setString(3, email);

            ps.executeUpdate();
            log.info("create OK: {}", entity);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Student", e);
        }
    }

    @Override
    public Student read(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("read requires a Student with non-null nif");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_DNI)) {

            ps.setString(1, entity.getNif());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student found = mapRow(rs);
                    log.info("read OK: {}", found);
                    return found;
                } else {
                    log.info("read: no student found with nif={}", entity.getNif());
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading Student nif=" + entity.getNif(), e);
        }
    }

    @Override
    public Student update(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("update requires a Student with non-null nif");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getNif());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Student not found for update: nif=" + entity.getNif());
            }

            log.info("update OK: {}", entity);
            return entity;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating Student nif=" + entity.getNif(), e);
        }
    }

    @Override
    public Student findAll(Student entity) {
        if (entity == null) {
            throw new IllegalArgumentException("findAll requires a Student");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            ps.setString(1, entity.getNif());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student found = mapRow(rs);
                    log.info("findAll OK: {}", found);
                    return found;
                } else {
                    log.info("findAll: no student found with nif={}", entity.getNif());
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Student", e);
        }

    }

    @Override
    public boolean delete(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("delete requires a Student with non-null nif");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setString(1, entity.getNif());

            int deleted = ps.executeUpdate();
            boolean ok = deleted > 0;

            log.info("delete {} for nif={}", ok ? "OK" : "NOOP", entity.getNif());
            return ok;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Student nif=" + entity.getNif(), e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setName(rs.getString("nombre"));
        s.setNif(rs.getString("nif"));
        s.setEmail(rs.getString("email")); // agregar email
        return s;
    }
}
