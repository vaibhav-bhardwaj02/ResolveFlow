package com.resolveflow.service.interfaces;

import com.resolveflow.entity.User;

public interface AuthenticationService {

    User register(User user);

    User login(String email,
               String password);

}