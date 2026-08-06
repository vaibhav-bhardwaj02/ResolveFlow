package com.resolveflow.service.impl;

import com.resolveflow.dto.category.CategoryRequestDTO;
import com.resolveflow.dto.category.CategoryResponseDTO;
import com.resolveflow.entity.Category;
import com.resolveflow.exception.ResourceNotFoundException;
import com.resolveflow.mapper.CategoryMapper;
import com.resolveflow.repository.CategoryRepository;
import com.resolveflow.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {

        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Category already exists.");
        }

        Category category = CategoryMapper.toEntity(requestDTO);

        Category savedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id : " + id));

        CategoryMapper.updateEntity(requestDTO, category);

        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponseDTO(updatedCategory);
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id : " + id));

        return CategoryMapper.toResponseDTO(category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id : " + id));

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDTO changeCategoryStatus(Long id, Boolean active) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id : " + id));

        category.setActive(active);

        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponseDTO(updatedCategory);
    }
}