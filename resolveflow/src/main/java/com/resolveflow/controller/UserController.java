package com.resolveflow.controller;

import com.resolveflow.dto.user.ChangePasswordDTO;
import com.resolveflow.dto.user.UpdateProfileDTO;
import com.resolveflow.dto.user.UserResponseDTO;
import com.resolveflow.security.UserPrincipal;
import com.resolveflow.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponseDTO response = userService.getUserById(principal.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileDTO updateProfileDTO
    ) {
        UserResponseDTO response = userService.updateProfile(principal.getId(), updateProfileDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO
    ) {
        userService.changePassword(principal.getId(), changePasswordDTO);
        return ResponseEntity.noContent().build();
    }
}