package com.resolveflow.mapper;

import com.resolveflow.dto.category.CategoryRequestDTO;
import com.resolveflow.dto.category.CategoryResponseDTO;
import com.resolveflow.entity.Category;

public class CategoryMapper {

    private CategoryMapper() {
        // Prevent instantiation
    }

    public static Category toEntity(CategoryRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        return category;
    }

    public static CategoryResponseDTO toResponseDTO(Category category) {

        if (category == null) {
            return null;
        }

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public static void updateEntity(CategoryRequestDTO dto, Category category) {

        if (dto == null || category == null) {
            return;
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }
    }
}