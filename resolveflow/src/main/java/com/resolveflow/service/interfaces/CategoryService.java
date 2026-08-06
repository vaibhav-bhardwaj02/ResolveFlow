package com.resolveflow.service;

import com.resolveflow.dto.category.CategoryRequestDTO;
import com.resolveflow.dto.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);

    CategoryResponseDTO getCategoryById(Long id);

    List<CategoryResponseDTO> getAllCategories();

    void deleteCategory(Long id);

    CategoryResponseDTO changeCategoryStatus(Long id, Boolean active);
}