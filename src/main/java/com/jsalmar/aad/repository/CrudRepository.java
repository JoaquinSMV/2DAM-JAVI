package com.jsalmar.aad.repository;

import com.jsalmar.aad.model.Student;

public interface CrudRepository<T> {

    T create(T entity);

    T read(T entity);

    T update(T entity);

    Student findAll(Student entity);

    boolean delete(T entity);

}
