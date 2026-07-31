package com.resolveflow.service.impl;

import com.resolveflow.entity.User;
import com.resolveflow.service.interfaces.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public User register(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User login(String email, String password) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}