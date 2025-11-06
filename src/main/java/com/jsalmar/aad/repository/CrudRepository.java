package com.jsalmar.aad.repository;

public interface CrudRepository<T> {

    T create(T entity);

    T read(T entity);

    T update(T entity);

    boolean delete(T entity);

}
