package com.cg.service;

import java.util.List;
import java.util.Optional;



public interface IGeneralStringService<T> {

    List<T> findAll();

    Optional<T> findById(String id);

    Boolean existById(String id);

    T save(T t);

    void delete(T t);

    void deleteById(String id);
}
