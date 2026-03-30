package com.skillsync.authservice.controller;

import com.skillsync.authservice.dto.AuthResponse;
import com.skillsync.authservice.dto.LoginRequest;
import com.skillsync.authservice.dto.UserRequest;
import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.service.JwtService;
import com.skillsync.authservice.service.RefreshTokenService;
import com.skillsync.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Service", description = "APIs for authentication and authorization")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(userRequest));
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @RequestParam String refreshToken) {
        RefreshToken token = refreshTokenService.verifyToken(refreshToken);
        User user = token.getUser();
        String newAccessToken = jwtService.generateToken(
                user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @Operation(summary = "Logout and invalidate refresh token")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestParam String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}