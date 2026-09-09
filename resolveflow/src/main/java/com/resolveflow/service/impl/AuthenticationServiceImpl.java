package com.resolveflow.service.impl;

import com.resolveflow.dto.auth.LoginRequestDTO;
import com.resolveflow.dto.auth.LoginResponseDTO;
import com.resolveflow.dto.auth.RegisterRequestDTO;
import com.resolveflow.dto.user.UserResponseDTO;
import com.resolveflow.entity.User;
import com.resolveflow.mapper.UserMapper;
import com.resolveflow.repository.UserRepository;
import com.resolveflow.service.interfaces.AuthenticationService;
import com.resolveflow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
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
}