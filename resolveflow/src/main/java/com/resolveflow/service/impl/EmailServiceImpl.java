package com.resolveflow.service.impl;

import com.resolveflow.service.interfaces.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}