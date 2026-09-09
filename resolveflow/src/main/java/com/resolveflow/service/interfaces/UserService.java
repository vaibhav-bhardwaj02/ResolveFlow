package com.resolveflow.service.interfaces;

import com.resolveflow.dto.user.ChangePasswordDTO;
import com.resolveflow.dto.user.UpdateProfileDTO;
import com.resolveflow.dto.user.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByEmail(String email);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateProfile(Long id, UpdateProfileDTO updateProfileDTO);

    void changePassword(Long id, ChangePasswordDTO changePasswordDTO);

    void deleteUser(Long id);
}