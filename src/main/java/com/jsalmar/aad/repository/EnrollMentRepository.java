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
    private final RowMapper<Enrollment> enrollmentRowMapper = (rs, rowNum) -> {

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(rs.getInt("id_alumno"));
        enrollment.setModuleId(rs.getInt("id_modulo"));
        enrollment.setEnrollmentDate(rs.getDate("fecha").toLocalDate());

        return enrollment;

    };
    private SimpleJdbcCall countEnrollmentsCall;

    public EnrollMentRepository(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;

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


    public int countEnrollmentsByProcedure(int studentId) {

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("studentId", studentId);

        if (countEnrollmentsCall == null) {
            countEnrollmentsCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("public")
                    .withProcedureName("count_enrollments")
                    .declareParameters(
                            //new SqlParameter("studentId", Types.INTEGER),
                            //new SqlOutParameter("total", Types.INTEGER)
                    );
        }

        var out = countEnrollmentsCall.execute(in);

        return (Integer) out.get("total");

    }
}