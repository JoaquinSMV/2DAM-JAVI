package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class ModuleRepository implements CrudRepository<Module> {

    private static final String SQL_INSERT = """
                    INSERT INTO modulo (codigo, nombre, horas) VALUES (?, ?, ?)
            """;

    private static final String SQL_FIND_ALL = """
                    SELECT id_modulo, codigo, nombre, horas FROM modulo
            """;
    private static final String SQL_FIND_BY_ID = """
            SELECT id_modulo, codigo, nombre, horas FROM modulo WHERE id_modulo = ?
            """;
    // Nota: La sentencia UPDATE original tenía WHERE id = ?. Se asume que 'id' es 'id_modulo'.
    private static final String SQL_UPDATE = """
            UPDATE modulo SET codigo = ?, nombre = ?, horas = ? WHERE id_modulo = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM modulo WHERE id_modulo = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Module> moduleRowMapper = (rs, rowNum) -> {
        Module m = new Module();
        m.setId(rs.getInt("id_modulo"));
        m.setCode(rs.getString("codigo"));
        m.setName(rs.getString("nombre"));
        m.setHours(rs.getInt("horas"));

        return m;

    };


    public ModuleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -----------------------------
    // Métodos del CrudRepository
    // -----------------------------

    @Override
    public Module create(Module m) {

        if (m == null) throw new IllegalArgumentException("Module cannot be null");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = jdbcTemplate.update(connection -> {

                    var ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, m.getCode());
                    ps.setString(2, m.getName());
                    ps.setInt(3, m.getHours());

                    return ps;

                }
                , keyHolder);

        if (affectedRows > 0) {

            m.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.info("Module inserted: {}", m);

            return m;

        }

        throw new RuntimeException("Error inserting module. No rows affected.");

    }

    @Override
    public Module read(Module entity) {
        if (entity == null || entity.getId() == null) {

            throw new IllegalArgumentException("Module and its ID cannot be null");
        }

        return findById(entity.getId());

    }

    @Override
    public Module update(Module m) {

        if (m == null || m.getId() == null) {

            throw new IllegalArgumentException("Module and its ID cannot be null for update");

        }

        int affectedRows = jdbcTemplate.update(SQL_UPDATE,

                m.getCode(),
                m.getName(),
                m.getHours(),
                m.getId()

        );

        if (affectedRows > 0) {

            log.info("Module updated: {}", m);

            return m;

        }

        log.warn("Module not updated, id not found: {}", m.getId());

        return null;

    }

    @Override
    public Module findAll(Module entity) {

        return null;

    }

    @Override
    public boolean delete(Module entity) {

        if (entity == null || entity.getId() == null) {

            throw new IllegalArgumentException("Module and its ID cannot be null");

        }


        return deleteById(entity.getId());

    }

    @Override
    public boolean validate(Module entity) {

        return entity != null && entity.getCode() != null && entity.getName() != null;

    }


    // -----------------------------
    // Métodos específicos
    // -----------------------------


    public List<Module> findAll() {

        List<Module> modules = jdbcTemplate.query(SQL_FIND_ALL, moduleRowMapper);

        log.info("Found {} modules", modules.size());

        return modules;

    }

    public Module findById(int id) {

        try {

            Module module = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, moduleRowMapper, id);
            log.info("Module found: {}", module);

            return module;

        } catch (EmptyResultDataAccessException e) {

            log.warn("Module not found with id: {}", id);

            return null;

        }
    }

    public boolean deleteById(int id) {

        int deleted = jdbcTemplate.update(SQL_DELETE, id);

        boolean ok = deleted > 0;

        if (ok) {

            log.info("Module deleted with id: {}", id);

        } else {

            log.warn("Module not deleted, id not found: {}", id);

        }

        return ok;
    }
}