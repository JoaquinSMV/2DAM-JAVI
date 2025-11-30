package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class ModuleRepository implements CrudRepository<Module> {

    private static final String SQL_INSERT = """
                    INSERT INTO "modulo" ("codigo", "nombre", "horas") VALUES (?, ?, ?)
            """;

    private static final String SQL_FIND_ALL = """
                    SELECT "id_modulo", "codigo", "nombre", "horas" FROM "modulo"
            """;
    private static final String SQL_FIND_BY_ID = """
            SELECT "id_modulo", "codigo", "nombre", "horas" FROM "modulo" WHERE "id_modulo" = ?
            """;
    private static final String SQL_UPDATE = """
            UPDATE "modulo" SET "codigo" = ?, "nombre" = ?, "horas" = ? WHERE "id_modulo" = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM "modulo" WHERE "id_modulo" = ?
            """;


    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Module> moduleRowMapper = (rs, rowNum) -> {
        Module module = new Module();
        module.setId(rs.getInt("id_modulo"));
        module.setCode(rs.getString("codigo"));
        module.setName(rs.getString("nombre"));
        module.setHours(rs.getInt("horas"));
        return module;
    };


    public ModuleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // -----------------------------
    // Métodos requeridos por CrudRepository
    // -----------------------------

    @Override
    public Module create(Module entity) {

        if (entity == null || entity.getCode() == null || entity.getName() == null) {
            throw new IllegalArgumentException("Module must have non-null code and name");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int inserted = jdbcTemplate.update(connection -> {

            var ps = connection.prepareStatement(SQL_INSERT, new String[]{"id_modulo"});
            
            ps.setString(1, entity.getCode());
            ps.setString(2, entity.getName());
            ps.setInt(3, entity.getHours());

            return ps;

        }, keyHolder);

        if (inserted == 0) {
            throw new IllegalStateException("Insert failed, no rows affected");
        }

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Module created with ID: {}", entity.getId());

        return entity;
    }

    @Override
    public Module read(Module entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Module update(Module entity) {

        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Module and its ID cannot be null");
        }

        int updated = jdbcTemplate.update(SQL_UPDATE,
                entity.getCode(),
                entity.getName(),
                entity.getHours(),
                entity.getId());

        boolean ok = updated > 0;
        log.info("Module update {} for id={}. Updated: {}", ok ? "OK" : "NOOP", entity.getId(), entity);

        return entity;
    }

    @Override
    public Module findAll(Module entity) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        log.info("Module delete {} for id={}", ok ? "OK" : "NOOP", id);

        return ok;
    }
}