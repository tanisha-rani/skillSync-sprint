package com.skillsync.authservice.controller;

import com.skillsync.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtService jwtService;

    // API Gateway calls this to validate token
    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(
            @RequestParam String token) {
        String email = jwtService.extractEmail(token);
        String role = jwtService.extractRole(token);
        return ResponseEntity.ok(Map.of(
                "email", email,
                "role", role,
                "valid", "true"
        ));
    }
}
