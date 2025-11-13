package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public abstract class ModuleRepository implements CrudRepository<Module> { //Lo he puesto abstracto porque me daba un error y me decia el mismo intellIJ que lo pusiera en abstracto

    private static final String SQL_INSERT = """
            INSERT INTO modulo (codigo, nombre, horas) VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_ALL = """
            SELECT id, codigo, nombre, horas FROM modulo
            """;
    private static final String SQL_FIND_BY_ID = """
            SELECT id, codigo, nombre, horas FROM modulo WHERE id = ?
            """;
    private static final String SQL_UPDATE = """
            UPDATE modulo SET codigo = ?, nombre = ?, horas = ? WHERE id = ?
            """;
    private static final String SQL_DELETE = """
            DELETE FROM modulo WHERE id = ?
            """;

    private final DataSource dataSource;

    public ModuleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // -----------------------------
    // Métodos del CrudRepository
    // -----------------------------
    @Override
    public Module create(Module entity) {
        if (entity == null) throw new IllegalArgumentException("Module cannot be null");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getCode());
            ps.setString(2, entity.getName());
            ps.setInt(3, entity.getHours());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                    }
                }
            }

            log.info("Module inserted: {}", entity);
            return entity;

        } catch (SQLException e) {
            log.error("Error inserting module", e);
            throw new RuntimeException("Error inserting module", e);
        }
    }

    @Override
    public Module read(Module entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Module and its ID cannot be null");
        }
        return findById(entity.getId());
    }

    @Override
    public Module update(Module entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Module and its ID cannot be null for update");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, entity.getCode());
            ps.setString(2, entity.getName());
            ps.setInt(3, entity.getHours());
            ps.setInt(4, entity.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                log.info("Module updated: {}", entity);
                return entity;
            }

            log.warn("Module not updated, id not found: {}", entity.getId());
            return null;

        } catch (SQLException e) {
            log.error("Error updating module", e);
            throw new RuntimeException("Error updating module", e);
        }
    }

    @Override
    public boolean delete(Module entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Module and its ID cannot be null");
        }
        return deleteById(entity.getId());
    }

    // -----------------------------
    // Métodos específicos
    // -----------------------------
    public List<Module> findAll() {
        List<Module> modules = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Module module = mapRow(rs);
                modules.add(module);
            }

            log.info("Found {} modules", modules.size());
            return modules;

        } catch (SQLException e) {
            log.error("Error finding all modules", e);
            throw new RuntimeException("Error finding all modules", e);
        }
    }

    public Module findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Module module = mapRow(rs);
                    log.info("Module found: {}", module);
                    return module;
                }
            }

            log.warn("Module not found with id: {}", id);
            return null;

        } catch (SQLException e) {
            log.error("Error finding module by id: {}", id, e);
            throw new RuntimeException("Error finding module by id", e);
        }
    }

    public boolean deleteById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                log.info("Module deleted with id: {}", id);
                return true;
            }

            log.warn("Module not deleted, id not found: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting module with id: {}", id, e);
            throw new RuntimeException("Error deleting module", e);
        }
    }

    // -----------------------------
    // Método auxiliar
    // -----------------------------
    private Module mapRow(ResultSet rs) throws SQLException {
        Module m = new Module();
        m.setId(rs.getInt("id"));
        m.setCode(rs.getString("codigo"));
        m.setName(rs.getString("nombre"));
        m.setHours(rs.getInt("horas"));
        return m;
    }
}
