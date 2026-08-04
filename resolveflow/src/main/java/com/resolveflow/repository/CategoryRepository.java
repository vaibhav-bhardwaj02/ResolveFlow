package com.resolveflow.repository;

import com.resolveflow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by unique name
    Optional<Category> findByName(String name);

    // Check whether a category already exists
    boolean existsByName(String name);

    // Find only active categories
    List<Category> findByActiveTrue();

    // Find categories by active status
    List<Category> findByActive(Boolean active);
}