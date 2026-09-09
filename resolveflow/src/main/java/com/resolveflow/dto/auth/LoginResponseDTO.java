package com.resolveflow.dto.auth;

import com.resolveflow.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;
}