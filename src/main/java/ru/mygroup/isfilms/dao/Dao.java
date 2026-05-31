package ru.mygroup.isfilms.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean update(T entity);

    boolean delete(ID id);

    long count();
}
