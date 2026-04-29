package com.skillsync.authservice.dto;

import com.skillsync.authservice.entity.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Name Should not be null")
    @Size(min = 3,message = "Name Should contain alteast 3 characters")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain 1 uppercase, 1 lowercase, 1 digit, 1 special character and be at least 8 characters long")

    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}
