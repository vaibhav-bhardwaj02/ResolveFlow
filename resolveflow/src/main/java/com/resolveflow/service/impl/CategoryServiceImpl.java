package com.resolveflow.service.impl;

import com.resolveflow.entity.Category;
import com.resolveflow.service.interfaces.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public Category save(Category category) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Category update(Category category) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Category> findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Category> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}