package com.resolveflow.service.interfaces;

import com.resolveflow.dto.auth.*;
import com.resolveflow.dto.user.UserResponseDTO;

public interface AuthenticationService {
    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    void forgotPassword(ForgotPasswordDTO forgotPasswordDTO);
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    void verifyEmail(VerifyEmailDTO verifyEmailDTO);
}