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
    private final DataSource dataSource;

    public StudentJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Student create(Student entity) {
        if (entity == null) throw new IllegalArgumentException("Student cannot be null");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // ✅ ORDEN CORRECTO según: INSERT INTO alumno (nif, nombre, email)
            ps.setString(1, entity.getNif());      // nif
            ps.setString(2, entity.getName());     // nombre
            ps.setString(3, entity.getEmail());    // email

            int affectedRows = ps.executeUpdate();

            // Obtener el ID generado
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    }
                }
            }

            log.info("create OK: {}", entity);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Student", e);
        }
    }

    @Override
    public Student read(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("read requires a Student with non-null NIF");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_DNI)) {

            ps.setString(1, entity.getNif());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student found = mapRow(rs);
                    log.info("Student found: {}", found);
                    return found;
                } else {
                    log.info("No student found with NIF={}", entity.getNif());
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
            throw new IllegalArgumentException("update requires a Student with non-null NIF");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            // ✅ ORDEN CORRECTO según: UPDATE alumno SET nombre = ?, email = ? WHERE nif = ?
            ps.setString(1, entity.getName());     // nombre
            ps.setString(2, entity.getEmail());    // email
            ps.setString(3, entity.getNif());      // nif (WHERE clause)

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Student not found for update: nif=" + entity.getNif());
            }
            log.info("Student updated: {}", entity);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Student nif=" + entity.getNif(), e);
        }
    }

    @Override
    public Student findAll(Student entity) {
        return null;
    }

    @Override
    public boolean delete(Student entity) {
        if (entity == null || entity.getNif() == null) {
            throw new IllegalArgumentException("delete requires a Student with non-null NIF");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setString(1, entity.getNif());
            int deleted = ps.executeUpdate();
            boolean ok = deleted > 0;

            log.info("Student delete {} for nif={}", ok ? "OK" : "NOOP", entity.getNif());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Student nif=" + entity.getNif(), e);
        }
    }

    // -----------------------------
    // Método requerido por CrudRepository
    // -----------------------------
    @Override
    public boolean validate(Student entity) {
        return entity != null && entity.getName() != null && entity.getNif() != null;
    }

    // -----------------------------
    // Auxiliar
    // -----------------------------
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id_alumno"));      // Mapear id_alumno → id
        s.setName(rs.getString("nombre"));    // Mapear nombre → name
        s.setNif(rs.getString("nif"));
        s.setEmail(rs.getString("email"));
        return s;
    }

    public Student findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student found = mapRow(rs);
                    log.info("Student found by ID: {}", found);
                    return found;
                } else {
                    log.info("No student found with ID={}", id);
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading Student id=" + id, e);
        }
    }
}
