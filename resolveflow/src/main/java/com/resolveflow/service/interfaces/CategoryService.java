package com.resolveflow.service.interfaces;

import com.resolveflow.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category save(Category category);

    Category update(Category category);

    void delete(Long id);

    Optional<Category> findById(Long id);

    List<Category> findAll();
}