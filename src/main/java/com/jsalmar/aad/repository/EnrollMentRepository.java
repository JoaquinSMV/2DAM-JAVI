package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class EnrollMentRepository {

    // Consultas SQL
    private static final String SQL_INSERT_ENROLLMENT = """
            INSERT INTO matricula (student_id, module_id, enrollment_date)
            VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_ALL = """
            SELECT id, student_id, module_id, enrollment_date FROM matricula
            """;
    private static final String SQL_FIND_BY_STUDENT = """
            SELECT id, student_id, module_id, enrollment_date FROM matricula
            WHERE student_id = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM matricula WHERE student_id = ? AND module_id = ?
            """;
    private static final String SQL_COUNT_ENROLLMENTS = """
            SELECT count_enrollments(?) AS total
            """;
    private final DataSource dataSource;

    // Constructor
    public EnrollMentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // -----------------------------
    // Métodos
    // -----------------------------

    // Crea una matrícula con varios módulos en una transacción
    public void createEnrollment(Enrollment enrollment, List<Module> modules) {
        if (enrollment == null || enrollment.getStudentId() == null) {
            throw new IllegalArgumentException("Enrollment and studentId cannot be null");
        }
        if (modules == null || modules.isEmpty()) return;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // iniciar transacción

            try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ENROLLMENT, PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (Module module : modules) {
                    ps.setInt(1, enrollment.getStudentId());
                    ps.setInt(2, module.getId());
                    // Conversión correcta de LocalDate a java.sql.Date
                    ps.setDate(3, java.sql.Date.valueOf(enrollment.getEnrollmentDate()));
                    ps.addBatch();
                }
                ps.executeBatch();

                // Obtener los IDs generados si es necesario
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    int index = 0;
                    while (generatedKeys.next()) {
                        // Si necesitas asignar los IDs a las matrículas
                        int generatedId = generatedKeys.getInt(1);
                        log.debug("Matrícula creada con ID: {}", generatedId);
                    }
                }

                conn.commit();
                log.info("Enrollment created for student {} with {} modules", enrollment.getStudentId(), modules.size());
            } catch (SQLException e) {
                conn.rollback();
                log.error("Error creating enrollment, transaction rolled back", e);
                throw new RuntimeException("Error creating enrollment", e);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("Error creating enrollment", e);
            throw new RuntimeException("Error creating enrollment", e);
        }
    }

    // Devuelve todas las matrículas
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                enrollments.add(mapRow(rs));
            }
            log.info("Found {} enrollments", enrollments.size());
            return enrollments;

        } catch (SQLException e) {
            log.error("Error fetching all enrollments", e);
            throw new RuntimeException("Error fetching enrollments", e);
        }
    }

    // Devuelve todas las matrículas de un estudiante
    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STUDENT)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRow(rs));
                }
            }
            log.info("Found {} enrollments for student {}", enrollments.size(), studentId);
            return enrollments;

        } catch (SQLException e) {
            log.error("Error fetching enrollments for student {}", studentId, e);
            throw new RuntimeException("Error fetching enrollments", e);
        }
    }

    // Elimina una matrícula específica
    public boolean delete(int studentId, int moduleId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, studentId);
            ps.setInt(2, moduleId);

            int affectedRows = ps.executeUpdate();
            boolean ok = affectedRows > 0;
            log.info("Enrollment delete {} for student {} and module {}", ok ? "OK" : "NOOP", studentId, moduleId);
            return ok;

        } catch (SQLException e) {
            log.error("Error deleting enrollment", e);
            throw new RuntimeException("Error deleting enrollment", e);
        }
    }

    // Cuenta matrículas de un estudiante usando función almacenada
    public int countEnrollments(int studentId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ENROLLMENTS)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
            return 0;

        } catch (SQLException e) {
            log.error("Error counting enrollments for student {}", studentId, e);
            throw new RuntimeException("Error counting enrollments", e);
        }
    }

    // -----------------------------
    // Auxiliar
    // -----------------------------
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id")); // ¡IMPORTANTE: No olvides el ID!
        e.setStudentId(rs.getInt("student_id"));
        e.setModuleId(rs.getInt("module_id"));
        // Conversión de java.sql.Date a LocalDate
        Date sqlDate = rs.getDate("enrollment_date");
        if (sqlDate != null) {
            e.setEnrollmentDate(sqlDate.toLocalDate());
        }
        return e;
    }
}