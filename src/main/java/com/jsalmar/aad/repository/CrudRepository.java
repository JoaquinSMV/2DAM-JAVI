package com.jsalmar.aad.repository;

public interface CrudRepository<T> {

    T create(T entity);

    T read(T entity);

    T update(T entity);

    T findAll(T entity);

    boolean delete(T entity);

    boolean validate(T entity);


}
