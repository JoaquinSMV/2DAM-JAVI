package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Enrollment;
import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@Slf4j
public class EnrollMentRepository {

    // Consultas SQL
    private static final String SQL_INSERT_ENROLLMENT = """
            INSERT INTO matricula (id_alumno, id_modulo, fecha) VALUES (?, ?, ?)
            """;

    private static final String SQL_FIND_ALL = """
            SELECT id_alumno, id_modulo, fecha FROM matricula
            """;

    private static final String SQL_FIND_BY_STUDENT = """
            SELECT id_alumno, id_modulo, fecha FROM matricula WHERE id_alumno = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM matricula WHERE id_alumno = ? AND id_modulo = ?
            """;

    private static final String SQL_COUNT_ENROLLMENTS_SELECT = """
            SELECT count_enrollments(?) AS total
            """;

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall simpleJdbcCall;

    private final RowMapper<Enrollment> enrollmentRowMapper = (rs, rowNum) ->
    {

        Enrollment e = new Enrollment();

        e.setStudentId(rs.getInt("id_alumno"));
        e.setModuleId(rs.getInt("id_modulo"));
        Date sqlDate = rs.getDate("fecha");

        if (sqlDate != null) {
            e.setEnrollmentDate(sqlDate.toLocalDate());
        }

        return e;

    };

    // Constructor: Inyectamos JdbcTemplate y configuramos SimpleJdbcCall
    public EnrollMentRepository(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("count_enrollments");

    }

    // -----------------------------
    // Métodos
    // -----------------------------

    public void createEnrollment(Enrollment enrollment, List<Module> modules) {

        if (enrollment == null || enrollment.getStudentId() == null) {

            throw new IllegalArgumentException("Enrollment and studentId cannot be null");

        }

        if (modules == null || modules.isEmpty()) return;

        int[][] affectedRows = jdbcTemplate.batchUpdate(SQL_INSERT_ENROLLMENT, modules, modules.size(), (ps, module) -> {

            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, module.getId());
            // Conversión de LocalDate a java.sql.Date
            ps.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));

        });

        log.info("Batch enrollment created for student {} with {} modules. Affected rows: {}", enrollment.getStudentId(), modules.size(), affectedRows.length);
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


    public int countEnrollmentsByProcedure(int studentId) {

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("studentId", studentId);

        Number total = simpleJdbcCall.executeFunction(Integer.class, in);

        int result = (total != null) ? total.intValue() : 0;
        log.info("Total enrollments for student {}: {}", studentId, result);

        return result;

    }
}