package com.jsalmar.aad.application;

public interface CustomService<T> {

    boolean validate(T entity);

}
