package com.resolveflow.controller;

import com.resolveflow.dto.category.CategoryRequestDTO;
import com.resolveflow.dto.category.CategoryResponseDTO;
import com.resolveflow.service.interfaces.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDTO createCategory(
            @Valid @RequestBody CategoryRequestDTO requestDTO) {

        return categoryService.createCategory(requestDTO);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {

        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryService.getAllCategories();
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {

        return categoryService.updateCategory(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{id}/status")
    public CategoryResponseDTO changeCategoryStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        return categoryService.changeCategoryStatus(id, active);
    }
}