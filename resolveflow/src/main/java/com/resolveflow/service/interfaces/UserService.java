package com.resolveflow.service.interfaces;

import com.resolveflow.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User update(User user);

    void delete(Long id);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}