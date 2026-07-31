package com.resolveflow.service.impl;

import com.resolveflow.entity.User;
import com.resolveflow.service.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User save(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User update(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findByEmail(String email) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}