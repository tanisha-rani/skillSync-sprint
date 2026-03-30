package com.skillsync.authservice.dto;

import com.skillsync.authservice.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}