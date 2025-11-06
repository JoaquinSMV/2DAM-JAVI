package com.jsalmar.aad.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ModuleRepository implements CrudRepository<Module> {

    /**
     * @param entity
     * @return
     */
    @Override
    public Module create(Module entity) {
        log.info("create: {}", entity.toString());
        return entity;
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public Module read(Module entity) {
        log.info("read: {}", entity.toString());
        return entity;
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public Module update(Module entity) {
        log.info("update: {}", entity.toString());
        return entity;
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public boolean delete(Module entity) {
        log.info("delete: {}", entity.toString());
        return true;
    }
}
