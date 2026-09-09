package com.resolveflow.service.interfaces;

import com.resolveflow.dto.auth.LoginRequestDTO;
import com.resolveflow.dto.auth.LoginResponseDTO;
import com.resolveflow.dto.auth.RegisterRequestDTO;
import com.resolveflow.dto.user.UserResponseDTO;

public interface AuthenticationService {

    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}