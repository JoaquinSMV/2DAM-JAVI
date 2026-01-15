package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class EnrollMentRepository {

    // Consultas SQL respetando las comillas de tu esquema
    private static final String SQL_INSERT_ENROLLMENT = """
            INSERT INTO "matricula" ("id_alumno", "id_modulo", "fecha") VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_ALL = """
            SELECT "id_alumno", "id_modulo", "fecha" FROM "matricula"
            """;
    private static final String SQL_FIND_BY_STUDENT = """
            SELECT "id_alumno", "id_modulo", "fecha" FROM "matricula" WHERE "id_alumno" = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM "matricula" WHERE "id_alumno" = ? AND "id_modulo" = ?
            """;
    private static final String SQL_COUNT_ENROLLMENTS_SELECT = """
            SELECT COUNT(*) FROM "matricula" WHERE "id_alumno" = ?
            """;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall countEnrollmentsCall;
    private final RowMapper<Enrollment> enrollmentRowMapper = (rs, rowNum) -> new Enrollment(
            null, // El ID de matrícula no existe en tu tabla (es PK compuesta)
            rs.getInt("id_alumno"),
            rs.getInt("id_modulo"),
            rs.getDate("fecha").toLocalDate()
    );

    public EnrollMentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        // CONFIGURACIÓN CLAVE PARA EL PUNTO K
        // Usamos declareParameters para evitar errores de "bad SQL grammar"
        this.countEnrollmentsCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("count_enrollments")
                .withoutProcedureColumnMetaDataAccess() // Importante: evita que Spring intente adivinar tipos
                .declareParameters(
                        new SqlParameter("student_id_param", Types.INTEGER),
                        new SqlOutParameter("total", Types.INTEGER)
                );
    }

    /**
     * Llamada al Procedimiento Almacenado (Requisito K de la rúbrica)
     */
    public int countEnrollmentsByProcedure(int studentId) {
        // El nombre "student_id_param" debe coincidir con el del SqlParameter declarado arriba
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("student_id_param", studentId);

        try {
            Map<String, Object> out = countEnrollmentsCall.execute(in);
            Object result = out.get("total");
            return (result != null) ? (Integer) result : 0;
        } catch (Exception e) {
            log.error("Error al llamar al procedimiento almacenado: {}", e.getMessage());
            // Opcional: lanzar la excepción si prefieres que la app falle
            // throw new RuntimeException("Error en procedimiento", e);
            return 0;
        }
    }

    public void createEnrollment(Enrollment enrollment, List<Module> modules) {
        for (Module module : modules) {
            int inserted = jdbcTemplate.update(SQL_INSERT_ENROLLMENT,
                    enrollment.getStudentId(),
                    module.getId(),
                    Date.valueOf(enrollment.getEnrollmentDate()));

            boolean ok = inserted > 0;
            log.info("Enrollment create {} for student {} and module {}", ok ? "OK" : "NOOP", enrollment.getStudentId(), module.getId());
        }
    }

    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = jdbcTemplate.query(SQL_FIND_ALL, enrollmentRowMapper);
        log.info("Found {} enrollments", enrollments.size());
        return enrollments;
    }

    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = jdbcTemplate.query(SQL_FIND_BY_STUDENT, enrollmentRowMapper, studentId);
        log.info("Found {} enrollments for student {}", enrollments.size(), studentId);
        return enrollments;
    }

    public boolean delete(int studentId, int moduleId) {
        int affectedRows = jdbcTemplate.update(SQL_DELETE, studentId, moduleId);
        boolean ok = affectedRows > 0;
        log.info("Enrollment delete {} for student {} and module {}", ok ? "OK" : "NOOP", studentId, moduleId);
        return ok;
    }

    public int countEnrollmentsBySelect(int studentId) {
        try {
            Integer total = jdbcTemplate.queryForObject(SQL_COUNT_ENROLLMENTS_SELECT, Integer.class, studentId);
            return (total != null) ? total : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}