package com.resolveflow.service.impl;

import com.resolveflow.dto.auth.*;
import com.resolveflow.dto.user.UserResponseDTO;
import com.resolveflow.entity.*;
import com.resolveflow.mapper.UserMapper;
import com.resolveflow.repository.*;
import com.resolveflow.service.interfaces.AuthenticationService;
import com.resolveflow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (userRepository.existsByPhoneNumber(registerRequestDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }

        User user = UserMapper.toEntity(registerRequestDTO);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        User savedUser = userRepository.save(user);

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationToken.setUser(savedUser);
        emailVerificationTokenRepository.save(verificationToken);

        System.out.println("[EMAIL] Verification link for " + savedUser.getEmail()
                + ": /api/auth/verify-email?token=" + verificationToken.getToken());

        return UserMapper.toResponseDTO(savedUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalStateException("Account is disabled.");
        }

        String token = jwtUtil.generateToken(user);

        return LoginResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);

        System.out.println("[EMAIL] Password reset link for " + user.getEmail()
                + ": /api/auth/reset-password?token=" + resetToken.getToken());
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetPasswordDTO.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public void verifyEmail(VerifyEmailDTO verifyEmailDTO) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findByToken(verifyEmailDTO.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired.");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }
}